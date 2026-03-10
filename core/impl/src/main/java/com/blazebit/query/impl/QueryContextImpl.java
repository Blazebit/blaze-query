/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import com.blazebit.query.QueryContext;
import com.blazebit.query.QueryException;
import com.blazebit.query.QuerySession;
import com.blazebit.query.impl.calcite.CalciteDataSource;
import com.blazebit.query.impl.calcite.DataFetcherTable;
import com.blazebit.query.impl.calcite.SubSchema;
import com.blazebit.query.impl.metamodel.MetamodelImpl;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;
import com.blazebit.query.spi.TypeConverter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterators.spliteratorUnknownSize;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class QueryContextImpl implements QueryContext {

	private final ConfigurationProviderImpl configurationProvider;
	private final MetamodelImpl metamodel;
	private final CalciteDataSource calciteDataSource;
	private final ImmutableList<TypeConverter> typeConverters;
	private volatile boolean closed;

	public QueryContextImpl(QueryContextBuilderImpl builder) {
		this.configurationProvider = new ConfigurationProviderImpl(
				ImmutableMap.copyOf( builder.propertyProviders ) );
		this.calciteDataSource = new CalciteDataSource( builder.getProperties() );
		this.metamodel = new MetamodelImpl(
				resolveSchemaObjects( builder, configurationProvider, calciteDataSource ) );
		this.typeConverters = ImmutableList.copyOf( builder.typeConverters );
	}

	private <T> ResultExtractor<T> getResultExtractor(
			ResultSet resultSet,
			TypedQueryImpl<T> query) {
		Type resultType = query.getResultType();
		ResultValueNormalizer normalizer = ResultValueNormalizer.create(
				resultSet, value -> convert( value, Object.class ) );
		if ( resultType == Object[].class ) {
			try {
				return (ResultExtractor<T>) new ObjectArrayExtractor(
						resultSet.getMetaData().getColumnCount(), normalizer );
			}
			catch (SQLException e) {
				throw new QueryException( "Couldn't access result set metadata", e,
						query.getQueryString()
				);
			}

		}

		if ( resultType == Map.class
				|| resultType instanceof ParameterizedType parameterizedType
				&& parameterizedType.getRawType() == Map.class ) {
			try {
				return (ResultExtractor<T>) new MapExtractor(
						resultSet.getMetaData().getColumnCount(), normalizer );
			}
			catch (SQLException e) {
				throw new QueryException( "Couldn't access result set metadata", e,
						query.getQueryString()
				);
			}
		}

		return new SingleObjectExtractor<>( resultType, normalizer );
	}

	private static ImmutableMap<String, SchemaObjectTypeImpl<?>> resolveSchemaObjects(
			QueryContextBuilderImpl builder,
			ConfigurationProviderImpl configurationProvider,
			CalciteDataSource calciteDataSource) {

		Map<String, SchemaProviderEntry> schemaProviderEntries = new HashMap<>();
		for ( QuerySchemaProvider schemaProvider : builder.schemaProviders ) {
			Set<? extends DataFetcher<?>> schemaObjects = schemaProvider.resolveSchemaObjects(
					configurationProvider );
			for ( DataFetcher<?> schemaObject : schemaObjects ) {
				SchemaProviderEntry providerEntry = schemaProviderEntries.put(
						schemaObject.getDataFormat().getType().getTypeName(),
						new SchemaProviderEntry( schemaObject, schemaProvider )
				);
				if ( providerEntry != null ) {
					throw new IllegalArgumentException(
							"Schema provider " + schemaProvider + " overwrites entry provided by "
									+ providerEntry.provider + " for type: "
									+ providerEntry.schemaObjectType.getCanonicalName() );
				}
			}
		}

		Map<String, SchemaObjectTypeImpl<?>> schemaObjects = new HashMap<>(
				schemaProviderEntries.size() + builder.schemaObjects.size()
						+ builder.schemaObjectNames.size()
		);
		SchemaPlus rootSchema = calciteDataSource.getRootSchema();
		for ( SchemaProviderEntry entry : schemaProviderEntries.values() ) {
			if ( !builder.schemaObjects.containsKey( entry.schemaObjectType.getCanonicalName() ) ) {
				//noinspection rawtypes,unchecked
				SchemaObjectTypeImpl<?> schemaObjectType = new SchemaObjectTypeImpl(
						schemaObjects.size(),
						entry.schemaObjectType,
						entry.dataFetcher
				);
				schemaObjects.put( entry.schemaObjectType.getCanonicalName(), schemaObjectType );
				addTable(
						rootSchema,
						entry.schemaObjectType,
						entry.dataFetcher,
						configurationProvider
				);
			}
		}

		for ( SchemaObjectTypeImpl<?> schemaObject : builder.schemaObjects.values() ) {
			//noinspection rawtypes,unchecked
			SchemaObjectTypeImpl<?> schemaObjectType = new SchemaObjectTypeImpl(
					schemaObjects.size(),
					schemaObject.getType(),
					schemaObject.getDataFetcher()
			);
			schemaObjects.put( schemaObject.getType().getCanonicalName(), schemaObjectType );
			addTable(
					rootSchema,
					schemaObject.getType(),
					schemaObject.getDataFetcher(),
					configurationProvider
			);
		}

		for ( Map.Entry<String, String> entry : builder.schemaObjectNames.entrySet() ) {
			SchemaObjectTypeImpl<?> schemaObjectType = schemaObjects.get( entry.getValue() );
			if ( schemaObjectType == null ) {
				throw new IllegalArgumentException( "Schema object alias [" + entry.getKey()
						+ "] refers to an unknown schema object name: " + entry.getValue() );
			}
			schemaObjects.put( entry.getKey(), schemaObjectType );
			addTable( rootSchema, entry.getKey(), getTable( rootSchema, entry.getValue() ) );
		}

		return ImmutableMap.copyOf( schemaObjects );
	}

	private static void addTable(
			SchemaPlus rootSchema,
			Class<?> schemaObjectType,
			DataFetcher<?> dataFetcher,
			ConfigurationProviderImpl configurationProvider) {
		Table table;
		if ( dataFetcher instanceof Table ) {
			table = (Table) dataFetcher;
		}
		else {
			table = new DataFetcherTable( schemaObjectType, dataFetcher, configurationProvider );
		}
		addTable( rootSchema, schemaObjectType.getCanonicalName(), table );
	}

	private static void addTable(SchemaPlus rootSchema, String qualifiedName, Table table) {
		SchemaPlus schema = rootSchema;
		String[] nameParts = qualifiedName.split( "\\." );
		for ( int i = 0; i < nameParts.length - 1; i++ ) {
			SchemaPlus subSchema = schema.getSubSchema( nameParts[i] );
			if ( subSchema == null ) {
				schema = schema.add( nameParts[i], new SubSchema() );
			}
			else {
				schema = subSchema;
			}
		}
		schema.add( nameParts[nameParts.length - 1], table );
	}

	private static Table getTable(SchemaPlus rootSchema, String qualifiedName) {
		SchemaPlus schema = rootSchema;
		String[] nameParts = qualifiedName.split( "\\." );
		for ( int i = 0; i < nameParts.length - 1; i++ ) {
			schema = schema.getSubSchema( nameParts[i] );
		}
		return schema.getTable( nameParts[nameParts.length - 1] );
	}

	@Override
	public QuerySession createSession(Map<String, Object> properties) {
		checkClosed();
		return new QuerySessionImpl( this, properties );
	}

	public ConfigurationProviderImpl getConfigurationProvider() {
		return configurationProvider;
	}

	public Connection createConnection() {
		try {
			return calciteDataSource.getConnection();
		}
		catch (SQLException e) {
			throw new RuntimeException( "Couldn't acquire connection", e );
		}
	}

	public <T> List<T> getResultList(TypedQueryImpl<T> query, PreparedStatement preparedStatement) {
		configurationProvider.setQuery( query );
		try (ResultSet resultSet = preparedStatement.executeQuery()) {
			ResultExtractor<T> extractor = getResultExtractor( resultSet, query );
			ArrayList<T> resultList = new ArrayList<>();
			while ( resultSet.next() ) {
				resultList.add( extractor.extract( resultSet ) );
			}
			return resultList;
		}
		catch (SQLException e) {
			throw new QueryException( "Error while executing query", e, query.getQueryString() );
		}
		finally {
			configurationProvider.unsetQuery();
		}
	}

	public <T> Stream<T> getResultStream(
			TypedQueryImpl<T> query,
			PreparedStatement preparedStatement) {
		configurationProvider.setQuery( query );
		try {
			ResultSetIterator<T> iterator = new ResultSetIterator<>(
					query,
					preparedStatement.executeQuery()
			);
			Spliterator<T> spliterator = spliteratorUnknownSize( iterator, Spliterator.NONNULL );
			Stream<T> stream = StreamSupport.stream( spliterator, false );
			return stream.onClose( iterator::close );
		}
		catch (SQLException e) {
			throw new QueryException( "Error while executing query", e, query.getQueryString() );
		}
		finally {
			configurationProvider.unsetQuery();
		}
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		checkClosed();
		if ( cls == CalciteDataSource.class ) {
			return (T) calciteDataSource;
		}
		else if ( SchemaPlus.class.isAssignableFrom( cls ) ) {
			return (T) calciteDataSource.getRootSchema();
		}
		throw new IllegalArgumentException( "Can't unwrap to: " + cls.getName() );
	}

	@Override
	public MetamodelImpl getMetamodel() {
		checkClosed();
		return metamodel;
	}

	@Override
	public boolean isOpen() {
		return !closed;
	}

	public void checkClosed() {
		if ( closed ) {
			throw new IllegalStateException( "QueryContext already closed" );
		}
	}

	@Override
	public void close() {
		checkClosed();
		closed = true;
	}

	private interface ResultExtractor<T> {

		T extract(ResultSet resultSet) throws SQLException;
	}

	private class ResultSetIterator<T> implements Iterator<T> {

		private final TypedQueryImpl<T> query;
		private final ResultSet resultSet;
		private final ResultExtractor<T> extractor;
		private boolean hasNext;

		public ResultSetIterator(TypedQueryImpl<T> query, ResultSet resultSet) {
			this.query = query;
			this.resultSet = resultSet;
			this.extractor = getResultExtractor( resultSet, query );
			advance();
		}

		private void advance() {
			try {
				hasNext = resultSet.next();
			}
			catch (SQLException e) {
				throw new QueryException( "Couldn't advance to next row", e, query.getQueryString() );
			}
		}

		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public T next() {
			if ( !hasNext ) {
				throw new NoSuchElementException();
			}

			T object;
			try {
				object = extractor.extract( resultSet );
			}
			catch (SQLException e) {
				throw new QueryException( "Couldn't extract tuple", e, query.getQueryString() );
			}
			advance();
			return object;
		}

		public void close() {
			try {
				resultSet.close();
			}
			catch (SQLException e) {
				throw new QueryException( "Error during result set closing", e,
						query.getQueryString()
				);
			}
		}
	}

	private class ObjectArrayExtractor implements ResultExtractor<Object[]> {

		private final int columnCount;
		private final ResultValueNormalizer normalizer;

		public ObjectArrayExtractor(int columnCount, ResultValueNormalizer normalizer) {
			this.columnCount = columnCount;
			this.normalizer = normalizer;
		}

		@Override
		public Object[] extract(ResultSet resultSet) throws SQLException {
			Object[] tuple = new Object[columnCount];
			for ( int i = 0; i < tuple.length; i++ ) {
				tuple[i] = normalizer.normalizeColumn( resultSet.getObject( i + 1 ), i );
			}
			return tuple;
		}
	}

	private class MapExtractor implements ResultExtractor<Map> {

		private final int columnCount;
		private final ResultValueNormalizer normalizer;

		public MapExtractor(int columnCount, ResultValueNormalizer normalizer) {
			this.columnCount = columnCount;
			this.normalizer = normalizer;
		}

		@Override
		public Map<String, Object> extract(ResultSet resultSet) throws SQLException {
			Map<String, Object> map = new LinkedHashMap<>();
			for ( int i = 0; i < columnCount; i++ ) {
				map.put( resultSet.getMetaData().getColumnLabel( i + 1 ),
						normalizer.normalizeColumn( resultSet.getObject( i + 1 ), i ) );
			}
			return map;
		}
	}

	private class SingleObjectExtractor<T> implements ResultExtractor<T> {

		private final Type resultType;
		private final ResultValueNormalizer normalizer;

		public SingleObjectExtractor(Type resultType, ResultValueNormalizer normalizer) {
			this.resultType = resultType;
			this.normalizer = normalizer;
		}

		@Override
		public T extract(ResultSet resultSet) throws SQLException {
			Object value = resultSet.getObject( 1 );
			if ( value instanceof Struct || value instanceof java.sql.Array
					|| value instanceof Object[] ) {
				return (T) normalizer.normalizeColumn( value, 0 );
			}
			return (T) convert( value, resultType );
		}
	}

	private Object convert(Object value, Type targetType) throws SQLException {
		if ( value == null ) {
			return null;
		}
		for ( TypeConverter typeConverter : typeConverters ) {
			if ( typeConverter.canConvert( value, targetType ) ) {
				return typeConverter.convert( value, targetType, this::convert );
			}
		}
		return DefaultTypeConverter.INSTANCE.convert( value, targetType, this::convert );
	}

	private static class SchemaProviderEntry {

		final Class<?> schemaObjectType;
		final DataFetcher<?> dataFetcher;
		final QuerySchemaProvider provider;

		public SchemaProviderEntry(
				DataFetcher<?> dataFetcher,
				QuerySchemaProvider provider) {
			if ( !(dataFetcher.getDataFormat().getType() instanceof Class<?>) ) {
				throw new IllegalArgumentException(
						"Field type unsupported: " + dataFetcher.getDataFormat().getType() );
			}
			this.schemaObjectType = (Class<?>) dataFetcher.getDataFormat().getType();
			this.dataFetcher = dataFetcher;
			this.provider = provider;
		}
	}
}

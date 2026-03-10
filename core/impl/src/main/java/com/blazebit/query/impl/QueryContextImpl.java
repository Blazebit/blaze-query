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
import com.google.common.collect.ImmutableMap;
import org.apache.calcite.avatica.AvaticaResultSet;
import org.apache.calcite.avatica.ColumnMetaData;
import org.apache.calcite.avatica.ColumnMetaData.ArrayType;
import org.apache.calcite.avatica.ColumnMetaData.AvaticaType;
import org.apache.calcite.avatica.ColumnMetaData.StructType;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Array;
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

	private static final Field COLUMN_META_DATA_LIST_FIELD;
	private final ConfigurationProviderImpl configurationProvider;
	private final MetamodelImpl metamodel;
	private final CalciteDataSource calciteDataSource;
	private volatile boolean closed;

	static {
		Field field;
		try {
			field = AvaticaResultSet.class.getDeclaredField( "columnMetaDataList" );
			field.setAccessible( true );
		}
		catch (Exception e) {
			field = null;
		}
		COLUMN_META_DATA_LIST_FIELD = field;
	}

	public QueryContextImpl(QueryContextBuilderImpl builder) {
		this.configurationProvider = new ConfigurationProviderImpl(
				ImmutableMap.copyOf( builder.propertyProviders ) );
		this.calciteDataSource = new CalciteDataSource( builder.getProperties() );
		this.metamodel = new MetamodelImpl(
				resolveSchemaObjects( builder, configurationProvider, calciteDataSource ) );
	}

	private static <T> ResultExtractor<T> getResultExtractor(
			AvaticaType[] columnTypes,
			ResultSet resultSet,
			TypedQueryImpl<T> query) {
		if ( columnTypes.length == 0 ) {
			try {
				int columnCount = resultSet.getMetaData().getColumnCount();
				if ( columnCount > 0 ) {
					columnTypes = new AvaticaType[columnCount];
				}
			}
			catch (SQLException e) {
				throw new QueryException( "Couldn't access result set metadata", e,
						query.getQueryString()
				);
			}
		}

		if ( query.getResultType() == Object[].class ) {
			return (ResultExtractor<T>) new ObjectArrayExtractor( columnTypes );
		}

		if ( query.getResultType() == Map.class
				|| query.getResultType() instanceof ParameterizedType parameterizedType
				&& parameterizedType.getRawType() == Map.class ) {
			return (ResultExtractor<T>) new MapExtractor( columnTypes );
		}

		return new SingleObjectExtractor<>( columnTypes.length == 0 ? null : columnTypes[0] );
	}

	private static AvaticaType[] extractColumnTypes(ResultSet resultSet) {
		if ( COLUMN_META_DATA_LIST_FIELD != null ) {
			try {
				if ( resultSet.isWrapperFor( AvaticaResultSet.class ) ) {
					AvaticaResultSet avaticaResultSet = resultSet.unwrap( AvaticaResultSet.class );
					List<ColumnMetaData> columns = (List<ColumnMetaData>) COLUMN_META_DATA_LIST_FIELD.get( avaticaResultSet );
					AvaticaType[] types = new AvaticaType[columns.size()];
					for ( int i = 0; i < columns.size(); i++ ) {
						types[i] = columns.get( i ).type;
					}
					return types;
				}
			}
			catch (Exception e) {
				// fallback
			}
		}
		return new AvaticaType[0];
	}

	static Object normalizeValue(Object value, AvaticaType type) {
		if ( value == null ) {
			return null;
		}
		if ( value instanceof Struct struct ) {
			try {
				Object[] attributes = struct.getAttributes();
				if ( type instanceof StructType structType
						&& structType.columns.size() == attributes.length ) {
					Map<String, Object> map = new LinkedHashMap<>( attributes.length );
					for ( int i = 0; i < attributes.length; i++ ) {
						ColumnMetaData field = structType.columns.get( i );
						String name = field.label != null ? field.label : field.columnName;
						map.put( name, normalizeValue( attributes[i], field.type ) );
					}
					return map;
				}
				List<Object> list = new ArrayList<>( attributes.length );
				for ( Object attr : attributes ) {
					list.add( normalizeValue( attr, null ) );
				}
				return list;
			}
			catch (SQLException e) {
				return value.toString();
			}
		}
		if ( value instanceof Array array ) {
			try {
				return normalizeValue( array.getArray(), type );
			}
			catch (SQLException e) {
				return value.toString();
			}
		}
		if ( value instanceof List<?> list ) {
			AvaticaType componentType = type instanceof ArrayType arrayType
					? arrayType.getComponent() : null;
			List<Object> result = new ArrayList<>( list.size() );
			for ( Object el : list ) {
				result.add( normalizeValue( el, componentType ) );
			}
			return result;
		}
		if ( value instanceof Object[] objArray ) {
			AvaticaType componentType = type instanceof ArrayType arrayType
					? arrayType.getComponent() : null;
			List<Object> result = new ArrayList<>( objArray.length );
			for ( Object el : objArray ) {
				result.add( normalizeValue( el, componentType ) );
			}
			return result;
		}
		return value;
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
			AvaticaType[] columnTypes = extractColumnTypes( resultSet );
			ResultExtractor<T> extractor = getResultExtractor( columnTypes, resultSet, query );
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
			ResultSet resultSet = preparedStatement.executeQuery();
			AvaticaType[] columnTypes = extractColumnTypes( resultSet );
			ResultSetIterator<T> iterator = new ResultSetIterator<>(
					query,
					columnTypes,
					resultSet
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

	private static class ResultSetIterator<T> implements Iterator<T> {

		private final TypedQueryImpl<T> query;
		private final ResultSet resultSet;
		private final ResultExtractor<T> extractor;
		private boolean hasNext;

		public ResultSetIterator(
				TypedQueryImpl<T> query,
				AvaticaType[] columnTypes,
				ResultSet resultSet) {
			this.query = query;
			this.resultSet = resultSet;
			this.extractor = getResultExtractor( columnTypes, resultSet, query );
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

	private static class ObjectArrayExtractor implements ResultExtractor<Object[]> {

		private final AvaticaType[] columnTypes;

		public ObjectArrayExtractor(AvaticaType[] columnTypes) {
			this.columnTypes = columnTypes;
		}

		@Override
		public Object[] extract(ResultSet resultSet) throws SQLException {
			Object[] tuple = new Object[columnTypes.length];
			for ( int i = 0; i < tuple.length; i++ ) {
				tuple[i] = normalizeValue( resultSet.getObject( i + 1 ), columnTypes[i] );
			}
			return tuple;
		}
	}

	private static class MapExtractor implements ResultExtractor<Map> {

		private final AvaticaType[] columnTypes;

		public MapExtractor(AvaticaType[] columnTypes) {
			this.columnTypes = columnTypes;
		}

		@Override
		public Map<String, Object> extract(ResultSet resultSet) throws SQLException {
			Map<String, Object> map = new LinkedHashMap<>();
			for ( int i = 0; i < columnTypes.length; i++ ) {
				map.put(
						resultSet.getMetaData().getColumnLabel( i + 1 ),
						normalizeValue( resultSet.getObject( i + 1 ), columnTypes[i] )
				);
			}
			return map;
		}
	}

	private static class SingleObjectExtractor<T> implements ResultExtractor<T> {
		private final AvaticaType type;

		public SingleObjectExtractor(AvaticaType type) {
			this.type = type;
		}

		@Override
		public T extract(ResultSet resultSet) throws SQLException {
			return (T) normalizeValue( resultSet.getObject( 1 ), type );
		}
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

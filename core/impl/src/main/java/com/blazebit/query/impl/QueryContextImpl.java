/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blazebit.query.impl;

import static java.util.Spliterators.spliteratorUnknownSize;

import com.blazebit.query.QueryContext;
import com.blazebit.query.QueryException;
import com.blazebit.query.QuerySession;
import com.blazebit.query.impl.calcite.DataFetcherTable;
import com.blazebit.query.impl.calcite.CalciteDataSource;
import com.blazebit.query.impl.calcite.SubSchema;
import com.blazebit.query.impl.metamodel.MetamodelImpl;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;
import com.google.common.collect.ImmutableMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class QueryContextImpl implements QueryContext {

    private final ConfigurationProviderImpl configurationProvider;
    private final MetamodelImpl metamodel;
    private final CalciteDataSource calciteDataSource;
    private volatile boolean closed;

    public QueryContextImpl(QueryContextBuilderImpl builder) {
        this.configurationProvider = new ConfigurationProviderImpl( ImmutableMap.copyOf( builder.propertyProviders ) );
        this.calciteDataSource = new CalciteDataSource();
        this.metamodel = new MetamodelImpl(resolveSchemaObjects( builder, configurationProvider, calciteDataSource ));
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
        } catch (SQLException e) {
            throw new RuntimeException( "Couldn't acquire connection", e );
        }
    }

    public <T> List<T> getResultList(TypedQueryImpl<T> query, PreparedStatement preparedStatement) {
        configurationProvider.setQuery(query);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            var resultSetHandler = new BeanListHandler<>(query.getResultClass());
            return resultSetHandler.handle(resultSet);
        } catch (SQLException e) {
            throw new QueryException( "Error while executing query", e, query.getQueryString() );
        } finally {
            configurationProvider.unsetQuery();
        }
    }

    public <T> Stream<T> getResultStream(TypedQueryImpl<T> query, PreparedStatement preparedStatement) {
        configurationProvider.setQuery(query);
        try {
            ResultSetIterator<T> iterator = new ResultSetIterator<>(query , preparedStatement.executeQuery() );
            Spliterator<T> spliterator = spliteratorUnknownSize( iterator, Spliterator.NONNULL );
            Stream<T> stream = StreamSupport.stream( spliterator, false );
            return stream.onClose( iterator::close );
        } catch (SQLException e) {
            throw new QueryException( "Error while executing query", e, query.getQueryString() );
        } finally {
            configurationProvider.unsetQuery();
        }
    }

    private static class ResultSetIterator<T> implements Iterator<T> {
        private final TypedQueryImpl<T> query;
        private final ResultSet resultSet;
        private final ResultSetHandler<T> extractor;
        private boolean hasNext;

        public ResultSetIterator(TypedQueryImpl<T> query, ResultSet resultSet) {
            this.query = query;
            this.resultSet = resultSet;
            this.extractor = new BeanHandler<>(query.getResultClass());
            advance();
        }

        private void advance() {
            try {
                hasNext = resultSet.next();
            } catch (SQLException e) {
                throw new QueryException( "Couldn't advance to next row", e, query.getQueryString() );
            }
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public T next() {
            T object;
            try {
                object = extractor.handle(resultSet);
            } catch (SQLException e) {
                throw new QueryException( "Couldn't extract tuple", e, query.getQueryString() );
            }
            advance();
            return object;
        }

        public void close() {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new QueryException( "Error during result set closing", e, query.getQueryString() );
            }
        }
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        checkClosed();
        if (cls == CalciteDataSource.class) {
            return (T) calciteDataSource;
        } else if (SchemaPlus.class.isAssignableFrom( cls ) ) {
            return (T) calciteDataSource.getRootSchema();
        }
        throw new IllegalArgumentException("Can't unwrap to: " + cls.getName() );
    }

    @Override
    public MetamodelImpl getMetamodel() {
        checkClosed();
        return metamodel;
    }

    @Override
    public boolean isOpen() {
        return closed;
    }

    public void checkClosed() {
        if (closed) {
            throw new IllegalStateException("QueryContext already closed");
        }
    }

    @Override
    public void close() {
        checkClosed();
        closed = true;
    }

    private static ImmutableMap<String, SchemaObjectTypeImpl<?>> resolveSchemaObjects(
            QueryContextBuilderImpl builder,
            ConfigurationProviderImpl configurationProvider,
            CalciteDataSource calciteDataSource) {
        HashMap<String, SchemaProviderEntry> schemaProviderEntries = new HashMap<>();
        for ( QuerySchemaProvider schemaProvider : builder.schemaProviders ) {
            Map<Class<?>, ? extends DataFetcher<?>> schemaObjects = schemaProvider.resolveSchemaObjects( configurationProvider );
            for ( Map.Entry<Class<?>, ? extends DataFetcher<?>> entry : schemaObjects.entrySet() ) {
                SchemaProviderEntry providerEntry = schemaProviderEntries.put(
                        entry.getKey().getCanonicalName(),
                        new SchemaProviderEntry( entry.getKey(), entry.getValue(), schemaProvider )
                );
                if ( providerEntry != null ) {
                    throw new IllegalArgumentException( "Schema provider " + schemaProvider + " overwrites entry provided by " + providerEntry.provider + " for type: " + providerEntry.schemaObjectType.getCanonicalName() );
                }
            }
        }
        HashMap<String, SchemaObjectTypeImpl<?>> schemaObjects = new HashMap<>(
                schemaProviderEntries.size() + builder.schemaObjects.size() + builder.schemaObjectNames.size()
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
                throw new IllegalArgumentException("Schema object alias [" + entry.getKey() + "] refers to an unknown schema object name: " + entry.getValue());
            }
            schemaObjects.put( entry.getKey(), schemaObjectType );
            addTable( rootSchema, entry.getKey(), getTable(rootSchema, entry.getValue()) );
        }
        return ImmutableMap.copyOf( schemaObjects );
    }

    private static void addTable(
            SchemaPlus rootSchema,
            Class<?> schemaObjectType,
            DataFetcher<?> dataFetcher,
            ConfigurationProviderImpl configurationProvider) {
        Table table;
        if ( dataFetcher instanceof Table) {
            table = (Table) dataFetcher;
        } else {
            table = new DataFetcherTable( schemaObjectType, dataFetcher, configurationProvider );
        }
        addTable( rootSchema, schemaObjectType.getCanonicalName(), table );
    }

    private static void addTable(SchemaPlus rootSchema, String qualifiedName, Table table) {
        SchemaPlus schema = rootSchema;
        String[] nameParts = qualifiedName.split( "\\." );
        for ( int i = 0; i < nameParts.length - 1; i++ ) {
            SchemaPlus subSchema = schema.getSubSchema( nameParts[i] );
            if (subSchema == null) {
                schema = schema.add( nameParts[i], new SubSchema() );
            } else {
                schema = subSchema;
            }
        }
        schema.add( nameParts[nameParts.length - 1], table);
    }

    private static Table getTable(SchemaPlus rootSchema, String qualifiedName) {
        SchemaPlus schema = rootSchema;
        String[] nameParts = qualifiedName.split( "\\." );
        for ( int i = 0; i < nameParts.length - 1; i++ ) {
            schema = schema.getSubSchema( nameParts[i] );
        }
        return schema.getTable( nameParts[nameParts.length - 1] );
    }

    private static class SchemaProviderEntry {
        final Class<?> schemaObjectType;
        final DataFetcher<?> dataFetcher;
        final QuerySchemaProvider provider;

        public SchemaProviderEntry(
                Class<?> schemaObjectType,
                DataFetcher<?> dataFetcher,
                QuerySchemaProvider provider) {
            this.schemaObjectType = schemaObjectType;
            this.dataFetcher = dataFetcher;
            this.provider = provider;
        }
    }
}

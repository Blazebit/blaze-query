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

import com.blazebit.query.spi.DataFetcherException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blazebit.query.QuerySession;
import com.blazebit.query.TypedQuery;
import com.blazebit.query.metamodel.SchemaObjectType;
import com.blazebit.query.spi.DataFetchContext;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class QuerySessionImpl implements QuerySession, DataFetchContext {

    private final QueryContextImpl queryContext;
    private final Map<SchemaObjectType<?>, List<?>> data;
    private Map<String, Object> properties;
    private Connection connection;
    private boolean closed;

    public QuerySessionImpl(QueryContextImpl queryContext, Map<String, Object> properties) {
        this.queryContext = queryContext;
        this.data = new HashMap<>();
        if (!properties.isEmpty()) {
            this.properties = properties;
        }
    }

    @Override
    public QueryContextImpl getContext() {
        return queryContext;
    }

    @Override
    public QuerySession getSession() {
        return this;
    }

    public Connection connection() {
        checkClosed();
        if (connection == null) {
            connection = queryContext.createConnection();
        }
        return connection;
    }

    @Override
    public <T> TypedQuery<T> createQuery(String queryString, Class<T> resultClass, Map<String, Object> properties) {
        checkClosed();
        return new TypedQueryImpl<>( this, queryString, resultClass, properties );
    }

    @Override
    public <T> List<? extends T> get(Class<T> schemaObjectTypeClass) {
        checkClosed();
        SchemaObjectTypeImpl<T> schemaObjectType = queryContext.getMetamodel().get( schemaObjectTypeClass );
        //noinspection unchecked
        return (List<? extends T>) data.get( schemaObjectType );
    }

    @Override
    public <T> List<? extends T> getOrFetch(Class<T> schemaObjectTypeClass) throws DataFetcherException {
        checkClosed();
        SchemaObjectTypeImpl<T> schemaObjectType = queryContext.getMetamodel().get( schemaObjectTypeClass );
        List<?> objects = data.get( schemaObjectType );
        if (objects == null) {
            data.put( schemaObjectType, objects = schemaObjectType.getDataFetcher().fetch( this ) );
        }
        //noinspection unchecked
        return (List<? extends T>) objects;
    }

    @Override
    public <T> List<? extends T> put(Class<T> schemaObjectType, List<? extends T> schemaObjects) {
        checkClosed();
        //noinspection unchecked
        return (List<? extends T>) data.put( queryContext.getMetamodel().get( schemaObjectType ), schemaObjects );
    }

    @Override
    public <T> List<? extends T> remove(Class<T> schemaObjectType) {
        checkClosed();
        //noinspection unchecked
        return (List<? extends T>) data.remove( queryContext.getMetamodel().get( schemaObjectType ) );
    }

    @Override
    public Set<SchemaObjectType<?>> getFetchedSchemaObjectTypes() {
        checkClosed();
        return data.keySet();
    }

    @Override
    public void clear() {
        checkClosed();
        data.clear();
    }

    public Object findLocalProperty(String key) {
        return properties == null ? null : properties.get(key);
    }

    @Override
    public <T> T findProperty(String key) {
        ConfigurationProviderImpl configurationProvider = queryContext.getConfigurationProvider();
        Object value = configurationProvider.hasCurrentQuery()
                ? configurationProvider.findProperty(key)
                : findLocalProperty(key);
        //noinspection unchecked
        return (T) value;
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        checkClosed();
        if (properties == null) {
            properties = new HashMap<>();
        }
        properties.put( propertyName, value );
    }

    @Override
    public Map<String, Object> getProperties() {
        checkClosed();
        return properties == null ? new HashMap<>() : new HashMap<>( properties );
    }

    public Map<String, Object> getPropertiesInternal() {
        return properties;
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        checkClosed();
        if (Connection.class.isAssignableFrom( cls )) {
            //noinspection unchecked
            return (T) connection();
        }
        throw new IllegalArgumentException("Can't unwrap to: " + cls.getName() );
    }

    @Override
    public boolean isOpen() {
        return closed;
    }

    public void checkClosed() {
        if (closed) {
            throw new IllegalStateException("QuerySession already closed");
        }
        queryContext.checkClosed();
    }

    @Override
    public void close() {
        checkClosed();
        closed = true;
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException( "Error while closing connection", e );
            }
        }
    }
}

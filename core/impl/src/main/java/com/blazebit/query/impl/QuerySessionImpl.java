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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.blazebit.query.Query;
import com.blazebit.query.QueryContext;
import com.blazebit.query.QuerySession;
import com.blazebit.query.metamodel.SchemaObjectType;
import com.blazebit.query.spi.DataFetchContext;
import com.google.common.collect.ImmutableMap;

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

	public Connection connection() {
		checkClosed();
		if (connection == null) {
			connection = queryContext.createConnection();
		}
		return connection;
	}

	@Override
	public Query createQuery(String queryString, Map<String, Object> properties) {
		checkClosed();
		return new QueryImpl( this, queryString, properties );
	}

	@Override
	public <T> List<? extends T> get(Class<T> schemaObjectTypeClass) {
		checkClosed();
		SchemaObjectTypeImpl<T> schemaObjectType = queryContext.getMetamodel().get( schemaObjectTypeClass );
		//noinspection unchecked
		return (List<? extends T>) data.get( schemaObjectType );
	}

	@Override
	public <T> List<? extends T> getOrFetch(Class<T> schemaObjectTypeClass) {
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

	@Override
	public <T> T findProperty(String key) {
		checkClosed();
		//noinspection unchecked
		return properties == null ? null : (T) properties.get( key );
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
			}
			catch (SQLException e) {
				throw new RuntimeException( "Error while closing connection", e );
			}
		}
	}
}

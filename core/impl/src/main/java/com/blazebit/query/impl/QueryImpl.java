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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.blazebit.query.Query;
import com.blazebit.query.QuerySession;
import com.blazebit.query.metamodel.SchemaObjectType;
import com.blazebit.query.spi.DataFetchContext;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class QueryImpl implements Query, DataFetchContext {

	private final QuerySessionImpl querySession;
	private final String queryString;
	private final PreparedStatement preparedStatement;
	private Map<String, Object> properties;

	public QueryImpl(QuerySessionImpl querySession, String queryString, Map<String, Object> properties) {
		this.querySession = querySession;
		this.queryString = queryString;
		try {
			this.preparedStatement = querySession.connection().prepareStatement(queryString);
		} catch (SQLException ex) {
			throw new IllegalArgumentException(ex);
		}
		if (!properties.isEmpty()) {
			this.properties = properties;
		}
	}

	@Override
	public QuerySession getSession() {
		return querySession;
	}

	@Override
	public Query setParameter(int position, Object value) {
		checkClosed();
		try {
			preparedStatement.setObject( position, value );
		}
		catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	@Override
	public List<Object[]> getResultList() {
		checkClosed();
		return querySession.getContext().getResultList(this, preparedStatement);
	}

	@Override
	public Stream<Object[]> getResultStream() {
		checkClosed();
		return querySession.getContext().getResultStream(this, preparedStatement);
	}

	@Override
	public String getQueryString() {
		return queryString;
	}

	@Override
	public <T> T findProperty(String key) {
		Object value = null;
		if (properties != null) {
			value = properties.get(key);
		}
		if (value == null) {
			value = querySession.findProperty( key );
		}
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
		Map<String, Object> sessionProperties = querySession.getPropertiesInternal();
		if ( properties == null ) {
			return sessionProperties;
		}
		if (sessionProperties == null) {
			return new HashMap<>( properties );
		}
		HashMap<String, Object> properties = new HashMap<>( sessionProperties.size() + this.properties.size() );
		properties.putAll( sessionProperties );
		properties.putAll( this.properties );
		return properties;
	}

	public void checkClosed() {
		querySession.checkClosed();
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		checkClosed();
		if (PreparedStatement.class.isAssignableFrom( cls )) {
			//noinspection unchecked
			return (T) preparedStatement;
		}
		throw new IllegalArgumentException("Can't unwrap to: " + cls.getName() );
	}
}

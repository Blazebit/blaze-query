/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.blazebit.query.QuerySession;
import com.blazebit.query.TypedQuery;
import com.blazebit.query.spi.DataFetchContext;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class TypedQueryImpl<T> implements TypedQuery<T>, DataFetchContext {

	private final QuerySessionImpl querySession;
	private final String queryString;
	private final Class<T> resultClass;
	private final PreparedStatement preparedStatement;
	private Map<String, Object> properties;

	public TypedQueryImpl(
			QuerySessionImpl querySession,
			String queryString,
			Class<T> resultClass,
			Map<String, Object> properties) {
		this.querySession = querySession;
		this.queryString = queryString;
		this.resultClass = resultClass;
		try {
			this.preparedStatement = querySession.connection().prepareStatement( queryString );
		}
		catch (SQLException ex) {
			throw new IllegalArgumentException( ex );
		}
		if ( !properties.isEmpty() ) {
			this.properties = properties;
		}
	}

	@Override
	public QuerySession getSession() {
		return querySession;
	}

	public Class<T> getResultClass() {
		return resultClass;
	}

	@Override
	public TypedQueryImpl<T> setParameter(int position, Object value) {
		checkClosed();
		try {
			preparedStatement.setObject( position, value );
		}
		catch (SQLException e) {
			throw new IllegalArgumentException( e );
		}
		return this;
	}

	@Override
	public List<T> getResultList() {
		checkClosed();
		return querySession.getContext().getResultList( this, preparedStatement );
	}

	@Override
	public Stream<T> getResultStream() {
		checkClosed();
		return querySession.getContext().getResultStream( this, preparedStatement );
	}

	@Override
	public String getQueryString() {
		return queryString;
	}

	@Override
	public <P> P findProperty(String key) {
		Object value = null;
		if ( properties != null ) {
			value = properties.get( key );
		}
		if ( value == null ) {
			value = querySession.findLocalProperty( key );
		}
		//noinspection unchecked
		return (P) value;
	}

	@Override
	public void setProperty(String propertyName, Object value) {
		checkClosed();
		if ( properties == null ) {
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
		if ( sessionProperties == null ) {
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
	public <C> C unwrap(Class<C> cls) {
		checkClosed();
		if ( PreparedStatement.class.isAssignableFrom( cls ) ) {
			//noinspection unchecked
			return (C) preparedStatement;
		}
		throw new IllegalArgumentException( "Can't unwrap to: " + cls.getName() );
	}
}

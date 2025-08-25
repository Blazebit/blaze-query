/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import com.blazebit.query.QuerySession;
import com.blazebit.query.TypeReference;
import com.blazebit.query.TypedQuery;
import com.blazebit.query.spi.DataFetchContext;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class TypedQueryImpl<T> implements TypedQuery<T>, DataFetchContext {

	private final QuerySessionImpl querySession;
	private final String queryString;
	private final TypeReference<T> resultType;
	private final PreparedStatement preparedStatement;
	private Map<String, Object> properties;

	public TypedQueryImpl(
			QuerySessionImpl querySession,
			String queryString,
			TypeReference<T> resultType,
			Map<String, Object> properties) {
		this.querySession = querySession;
		this.queryString = queryString;
		this.resultType = resultType;
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

	public Type getResultType() {
		return resultType.getType();
	}

	@Override
	public TypedQueryImpl<T> setParameter(int position, Object value) {
		checkClosed();
		try {
			if ( value == null ) {
				preparedStatement.setNull( position, java.sql.Types.NULL );
			}
			if ( value instanceof Character characterValue ) {
				preparedStatement.setString( position, String.valueOf( characterValue ) );
			}
			if ( value instanceof String stringValue ) {
				preparedStatement.setString( position, stringValue );
			}
			else if ( value instanceof Long longValue ) {
				preparedStatement.setLong( position, longValue );
			}
			else if ( value instanceof Integer integerValue ) {
				preparedStatement.setInt( position, integerValue );
			}
			else if ( value instanceof Short shortValue ) {
				preparedStatement.setShort( position, shortValue );
			}
			else if ( value instanceof Byte byteValue ) {
				preparedStatement.setByte( position, byteValue );
			}
			else if ( value instanceof Float floatValue ) {
				preparedStatement.setFloat( position, floatValue );
			}
			else if ( value instanceof Double doubleValue ) {
				preparedStatement.setDouble( position, doubleValue );
			}
			else if ( value instanceof Boolean booleanValue ) {
				preparedStatement.setBoolean( position, booleanValue );
			}
			else if ( value instanceof LocalDateTime localDateTime ) {
				preparedStatement.setTimestamp( position, Timestamp.valueOf( localDateTime ) );
			}
			else if ( value instanceof LocalDate localDate ) {
				preparedStatement.setDate( position, Date.valueOf( localDate ) );
			}
			else if ( value instanceof LocalTime localTime ) {
				preparedStatement.setTime( position, Time.valueOf( localTime ) );
			}
			else if ( value instanceof Instant instant ) {
				preparedStatement.setTimestamp( position, Timestamp.from( instant ) );
			}
			else if ( value instanceof OffsetDateTime offsetDateTime ) {
				preparedStatement.setTimestamp( position, Timestamp.from( offsetDateTime.toInstant() ) );
			}
			else if ( value instanceof ZonedDateTime zonedDateTime ) {
				preparedStatement.setTimestamp( position, Timestamp.from( zonedDateTime.toInstant() ) );
			}
			else if ( value instanceof BigDecimal bigDecimalValue ) {
				preparedStatement.setBigDecimal( position, bigDecimalValue );
			}
			else if ( value instanceof byte[] bytes ) {
				preparedStatement.setBytes( position, bytes );
			}
			else {
				preparedStatement.setObject( position, value );
			}
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

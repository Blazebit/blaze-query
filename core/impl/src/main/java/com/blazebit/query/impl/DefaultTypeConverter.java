/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import com.blazebit.query.spi.TypeConverter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Handles scalar type conversions between Java types.
 * Composite JDBC types (Struct, Array, Object[]) are handled by
 * {@link ResultValueNormalizer} before reaching this converter.
 *
 * @author Max Hovens
 * @since 2.2.0
 */
public class DefaultTypeConverter implements TypeConverter {

	public static final DefaultTypeConverter INSTANCE = new DefaultTypeConverter();

	private DefaultTypeConverter() {
	}

	@Override
	public boolean canConvert(Object value, Type targetType) {
		return true;
	}

	@Override
	public Object convert(Object value, Type targetType, Context context) throws SQLException {
		if ( value == null ) {
			return null;
		}
		if ( targetType instanceof Class<?> targetClass ) {
			if ( targetClass != Object.class && targetClass.isInstance( value ) ) {
				return value;
			}
			if ( targetClass == Object.class ) {
				return unwrapSqlScalar( value );
			}
			if ( targetClass.isEnum() ) {
				return Enum.valueOf( (Class<? extends Enum>) targetClass, value.toString() );
			}
			Object result = convertDateTime( value, targetClass );
			if ( result != null ) {
				return result;
			}
			result = convertUuid( value, targetClass );
			if ( result != null ) {
				return result;
			}
			if ( Number.class.isAssignableFrom( targetClass ) || targetClass.isPrimitive() ) {
				result = convertNumeric( value, targetClass );
				if ( result != null ) {
					return result;
				}
			}
		}
		return value;
	}

	private Object unwrapSqlScalar(Object value) {
		if ( value instanceof Timestamp timestamp ) {
			return timestamp.toInstant();
		}
		if ( value instanceof Date date ) {
			return date.toLocalDate();
		}
		if ( value instanceof Time time ) {
			return time.toLocalTime();
		}
		return value;
	}

	private Object convertDateTime(Object value, Class<?> targetClass) {
		if ( targetClass == Instant.class ) {
			if ( value instanceof Timestamp timestamp ) {
				return timestamp.toInstant();
			}
			if ( value instanceof Long l ) {
				return Instant.ofEpochMilli( l );
			}
		}
		if ( targetClass == LocalDateTime.class && value instanceof Timestamp timestamp ) {
			return timestamp.toLocalDateTime();
		}
		if ( targetClass == LocalDate.class && value instanceof Date date ) {
			return date.toLocalDate();
		}
		if ( targetClass == LocalTime.class && value instanceof Time time ) {
			return time.toLocalTime();
		}
		if ( targetClass == ZonedDateTime.class && value instanceof Timestamp timestamp ) {
			return timestamp.toInstant().atZone( ZoneOffset.UTC );
		}
		if ( targetClass == OffsetDateTime.class && value instanceof Timestamp timestamp ) {
			return timestamp.toInstant().atOffset( ZoneOffset.UTC );
		}
		if ( targetClass == OffsetTime.class && value instanceof Time time ) {
			return time.toLocalTime().atOffset( ZoneOffset.UTC );
		}
		if ( targetClass == Duration.class && value instanceof Long l ) {
			return Duration.ofMillis( l );
		}
		if ( targetClass == Period.class && value instanceof Integer i ) {
			return Period.ofMonths( i );
		}
		return null;
	}

	private Object convertUuid(Object value, Class<?> targetClass) {
		if ( targetClass != UUID.class ) {
			return null;
		}
		if ( value instanceof String s ) {
			return UUID.fromString( s );
		}
		if ( value instanceof byte[] bytes ) {
			return UUID.nameUUIDFromBytes( bytes );
		}
		return null;
	}

	private Object convertNumeric(Object value, Class<?> targetClass) {
		if ( targetClass == BigDecimal.class ) {
			if ( value instanceof Double d ) {
				return BigDecimal.valueOf( d );
			}
			if ( value instanceof Long l ) {
				return BigDecimal.valueOf( l );
			}
			if ( value instanceof Integer i ) {
				return BigDecimal.valueOf( i );
			}
			if ( value instanceof BigInteger bi ) {
				return new BigDecimal( bi );
			}
			if ( value instanceof String s ) {
				return new BigDecimal( s );
			}
		}
		if ( targetClass == BigInteger.class ) {
			if ( value instanceof Long l ) {
				return BigInteger.valueOf( l );
			}
			if ( value instanceof Integer i ) {
				return BigInteger.valueOf( i );
			}
			if ( value instanceof String s ) {
				return new BigInteger( s );
			}
			if ( value instanceof BigDecimal bd ) {
				return bd.toBigInteger();
			}
		}
		if ( targetClass == Long.class || targetClass == long.class ) {
			if ( value instanceof Number n ) {
				return n.longValue();
			}
			if ( value instanceof String s ) {
				return Long.parseLong( s );
			}
		}
		if ( targetClass == Integer.class || targetClass == int.class ) {
			if ( value instanceof Number n ) {
				return n.intValue();
			}
			if ( value instanceof String s ) {
				return Integer.parseInt( s );
			}
		}
		if ( targetClass == Double.class || targetClass == double.class ) {
			if ( value instanceof Number n ) {
				return n.doubleValue();
			}
			if ( value instanceof String s ) {
				return Double.parseDouble( s );
			}
		}
		if ( targetClass == Float.class || targetClass == float.class ) {
			if ( value instanceof Number n ) {
				return n.floatValue();
			}
			if ( value instanceof String s ) {
				return Float.parseFloat( s );
			}
		}
		if ( targetClass == Short.class || targetClass == short.class ) {
			if ( value instanceof Number n ) {
				return n.shortValue();
			}
			if ( value instanceof String s ) {
				return Short.parseShort( s );
			}
		}
		if ( targetClass == Byte.class || targetClass == byte.class ) {
			if ( value instanceof Number n ) {
				return n.byteValue();
			}
			if ( value instanceof String s ) {
				return Byte.parseByte( s );
			}
		}
		return null;
	}
}

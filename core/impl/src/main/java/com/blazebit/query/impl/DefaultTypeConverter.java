/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import com.blazebit.query.spi.TypeConverter;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;
import java.sql.Struct;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
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
		if ( targetType instanceof Class<?> ) {
			Class<?> targetClass = (Class<?>) targetType;
			if ( targetClass == Object.class ) {
				if ( value instanceof Timestamp timestamp ) {
					return timestamp.toInstant();
				}
				if ( value instanceof Date date ) {
					return date.toLocalDate();
				}
				if ( value instanceof Time time ) {
					return time.toLocalTime();
				}
				if ( value instanceof java.sql.Array array ) {
					Object[] elements = (Object[]) array.getArray();
					List<Object> convertedElements = new ArrayList<>( elements.length );
					for ( Object element : elements ) {
						convertedElements.add( context.convert( element, Object.class ) );
					}
					return convertedElements;
				}
				if ( value instanceof Struct struct ) {
					return context.convert( struct.getAttributes(), Object.class );
				}
				if ( value instanceof Object[] objectArray ) {
					Object[] convertedElements = new Object[objectArray.length];
					for ( int i = 0; i < objectArray.length; i++ ) {
						convertedElements[i] = context.convert( objectArray[i], Object.class );
					}
					return convertedElements;
				}
				return value;
			}
			if ( targetClass.isInstance( value ) ) {
				return value;
			}
			if ( targetClass.isEnum() ) {
				return Enum.valueOf( (Class<? extends Enum>) targetClass, value.toString() );
			}
			if ( targetClass == Instant.class ) {
				if ( value instanceof Timestamp timestamp ) {
					return timestamp.toInstant();
				}
				if ( value instanceof Long l ) {
					return Instant.ofEpochMilli( l );
				}
			}
			if ( targetClass == LocalDateTime.class ) {
				if ( value instanceof Timestamp timestamp ) {
					return timestamp.toLocalDateTime();
				}
			}
			if ( targetClass == LocalDate.class ) {
				if ( value instanceof Date date ) {
					return date.toLocalDate();
				}
			}
			if ( targetClass == LocalTime.class ) {
				if ( value instanceof Time time ) {
					return time.toLocalTime();
				}
			}
			if ( targetClass == ZonedDateTime.class ) {
				if ( value instanceof Timestamp timestamp ) {
					return timestamp.toInstant().atZone( ZoneOffset.UTC );
				}
			}
			if ( targetClass == OffsetDateTime.class ) {
				if ( value instanceof Timestamp timestamp ) {
					return timestamp.toInstant().atOffset( ZoneOffset.UTC );
				}
			}
			if ( targetClass == OffsetTime.class ) {
				if ( value instanceof Time time ) {
					return time.toLocalTime().atOffset( ZoneOffset.UTC );
				}
			}
			if ( targetClass == Duration.class ) {
				if ( value instanceof Long l ) {
					return Duration.ofMillis( l );
				}
			}
			if ( targetClass == Period.class ) {
				if ( value instanceof Integer i ) {
					return Period.ofMonths( i );
				}
			}
			if ( targetClass == UUID.class ) {
				if ( value instanceof String s ) {
					return UUID.fromString( s );
				}
				if ( value instanceof byte[] bytes ) {
					return UUID.nameUUIDFromBytes( bytes );
				}
			}
			if ( Number.class.isAssignableFrom( targetClass ) || targetClass.isPrimitive() ) {
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
			}
		}
		if ( value instanceof java.sql.Array array ) {
			Object[] elements = (Object[]) array.getArray();
			Type elementType = Object.class;
			Class<?> rawTargetClass = null;
			if ( targetType instanceof ParameterizedType parameterizedType ) {
				rawTargetClass = (Class<?>) parameterizedType.getRawType();
				elementType = parameterizedType.getActualTypeArguments()[0];
			}
			else if ( targetType instanceof Class<?> ) {
				rawTargetClass = (Class<?>) targetType;
			}

			List<Object> convertedElements = new ArrayList<>( elements.length );
			for ( Object element : elements ) {
				convertedElements.add( context.convert( element, elementType ) );
			}
			if ( rawTargetClass == null || List.class.isAssignableFrom( rawTargetClass ) ) {
				return convertedElements;
			}
			if ( Set.class.isAssignableFrom( rawTargetClass ) ) {
				return new HashSet<>( convertedElements );
			}
		}
		if ( value instanceof Struct struct ) {
			return context.convert( struct.getAttributes(), targetType );
		}
		if ( value instanceof Object[] objectArray ) {
			if ( targetType instanceof Class<?> targetClass && targetClass.isArray() ) {
				Class<?> componentType = targetClass.getComponentType();
				Object convertedArray = Array.newInstance( componentType, objectArray.length );
				for ( int i = 0; i < objectArray.length; i++ ) {
					Array.set( convertedArray, i, context.convert( objectArray[i], componentType ) );
				}
				return convertedArray;
			}
			Object[] convertedElements = new Object[objectArray.length];
			for ( int i = 0; i < objectArray.length; i++ ) {
				convertedElements[i] = context.convert( objectArray[i], Object.class );
			}
			return convertedElements;
		}
		return value;
	}
}

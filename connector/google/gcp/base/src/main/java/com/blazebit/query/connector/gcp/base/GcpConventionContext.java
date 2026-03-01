/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.base;

import com.blazebit.query.connector.base.ConventionContext;
import com.blazebit.query.connector.base.ConvertingFieldAccessor;
import com.blazebit.query.spi.DataFormatFieldAccessor;
import com.google.protobuf.ByteString;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;

/**
 * A method filter to exclude internal and cyclic methods from the GCP models.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class GcpConventionContext implements ConventionContext {

	public static final ConventionContext INSTANCE = new GcpConventionContext();

	private static final Method CONVERT_DURATION;
	private static final Method CONVERT_TIMESTAMP;

	static {
		try {
			CONVERT_DURATION = GcpTypeConverters.class.getMethod( "convertDuration", com.google.protobuf.Duration.class );
			CONVERT_TIMESTAMP = GcpTypeConverters.class.getMethod( "convertTimestamp", com.google.protobuf.Timestamp.class );
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException( e );
		}
	}

	private GcpConventionContext() {
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		final Method method = (Method) member;
		if ( method.getReturnType() == ByteString.class
				|| com.google.protobuf.ProtocolStringList.class.isAssignableFrom(method.getReturnType())
				|| method.getName().endsWith( "OrBuilder" )
				|| method.getName().endsWith( "OrBuilderList" )
				|| method.getDeclaringClass().getPackageName().startsWith( "com.google.protobuf" ) ) {
			return null;
		}
		return switch ( method.getName() ) {
			case "getDefaultInstanceForType", "getParserForType", "getSerializedSize" -> null;
			default -> this;
		};
	}

	@Override
	public Class<?> resolveType(Class<?> typeClass) {
		if ( typeClass == com.google.protobuf.Duration.class ) {
			return Duration.class;
		}
		if ( typeClass == com.google.protobuf.Timestamp.class ) {
			return Instant.class;
		}
		return typeClass;
	}

	@Override
	public DataFormatFieldAccessor createConvertingAccessor(
			DataFormatFieldAccessor accessor, Class<?> sourceType, Class<?> targetType) {
		if ( sourceType == com.google.protobuf.Duration.class ) {
			return new ConvertingFieldAccessor( accessor, CONVERT_DURATION );
		}
		if ( sourceType == com.google.protobuf.Timestamp.class ) {
			return new ConvertingFieldAccessor( accessor, CONVERT_TIMESTAMP );
		}
		return accessor;
	}
}

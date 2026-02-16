/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.base;

import com.blazebit.query.connector.base.ConventionContext;
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
			return o -> {
				com.google.protobuf.Duration d = (com.google.protobuf.Duration) accessor.get( o );
				if ( d == null || d.equals( com.google.protobuf.Duration.getDefaultInstance() ) ) {
					return null;
				}
				return Duration.ofSeconds( d.getSeconds(), d.getNanos() );
			};
		}
		if ( sourceType == com.google.protobuf.Timestamp.class ) {
			return o -> {
				com.google.protobuf.Timestamp t = (com.google.protobuf.Timestamp) accessor.get( o );
				if ( t == null || t.equals( com.google.protobuf.Timestamp.getDefaultInstance() ) ) {
					return null;
				}
				return Instant.ofEpochSecond( t.getSeconds(), t.getNanos() );
			};
		}
		return accessor;
	}
}

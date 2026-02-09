/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.base;

import com.blazebit.query.connector.base.ConventionContext;
import com.google.protobuf.ByteString;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

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
}

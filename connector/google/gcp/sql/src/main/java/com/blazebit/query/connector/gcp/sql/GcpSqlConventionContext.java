/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.sql;

import com.blazebit.query.connector.base.ConventionContext;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * Convention context for GCP Cloud SQL models (google-api-services-sqladmin).
 * Filters out internal Google API client methods from {@code GenericJson} base classes.
 *
 * @author Martijn Sprengers
 * @since 2.3.0
 */
public class GcpSqlConventionContext implements ConventionContext {

	public static final GcpSqlConventionContext INSTANCE = new GcpSqlConventionContext();

	private GcpSqlConventionContext() {
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		final Method method = (Method) member;
		final Class<?> returnType = method.getReturnType();
		// Filter methods from Google API client internal classes
		final String declaringPackage = method.getDeclaringClass().getPackageName();
		if ( declaringPackage.startsWith( "com.google.api.client" )
				|| declaringPackage.startsWith( "java.util" )
				|| declaringPackage.startsWith( "java.lang" ) ) {
			return null;
		}
		// Filter methods returning Class or Class[] (reflection/internal)
		if ( returnType == Class.class || returnType == Class[].class ) {
			return null;
		}
		if ( returnType.isArray() && returnType.getComponentType() == Class.class ) {
			return null;
		}
		// Filter methods whose return type is in Google API client internal packages
		final String returnPackage = returnType.getPackageName();
		if ( returnPackage.startsWith( "com.google.api.client" ) ) {
			return null;
		}
		return this;
	}
}

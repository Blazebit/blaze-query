/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.devops;

import com.blazebit.query.connector.base.ConventionContext;
import com.blazebit.query.connector.devops.model.AbstractOpenApiSchema;
import com.blazebit.query.connector.devops.model.ReferenceLinks;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.8
 */
public class DevopsConventionContext implements ConventionContext {

	public static final ConventionContext INSTANCE = new DevopsConventionContext();

	private DevopsConventionContext() {
	}

	@Override
	public boolean nullOnException(Method method) {
		// An OpenAPI schema is a union type, so some getters may throw exceptions based on actual instance
		return AbstractOpenApiSchema.class.isAssignableFrom( method.getDeclaringClass() );
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		if ( AbstractOpenApiSchema.class.isAssignableFrom( concreteClass ) ) {
			return switch ( member.getName() ) {
				case "getSchemas", "getActualInstance", "getActualInstanceRecursively", "getSchemaType", "isNullable" ->
						null;
				default -> this;
			};
		}
		if ( member instanceof Method method ) {
			if ( method.getName().endsWith( "_JsonNullable" ) ) {
				return null;
			}
			if ( ReferenceLinks.class.equals( method.getReturnType() ) ) {
				return null;
			}
		}
		return this;
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud.admin;


import com.blazebit.query.connector.base.ConventionContext;
import com.blazebit.query.connector.jira.cloud.admin.model.AbstractOpenApiSchema;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * A method filter to exclude internal methods from the Jira models.
 *
 * @author Dimitar Prisadnikov
 * @since 1.0.8
 */
public class JiraCloudAdminConventionContext implements ConventionContext {

	public static final ConventionContext INSTANCE = new JiraCloudAdminConventionContext();

	private JiraCloudAdminConventionContext() {
	}

	@Override
	public boolean nullOnException(Method method) {
		// An OpenAPI schema is a union type, so some getters may throw exceptions based on actual instance
		return AbstractOpenApiSchema.class.isAssignableFrom( method.getDeclaringClass() );
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		if ( AbstractOpenApiSchema.class.isAssignableFrom( concreteClass ) ) {
			switch ( member.getName() ) {
				case "getSchemas":
				case "getActualInstance":
				case "getActualInstanceRecursively":
				case "getSchemaType":
				case "isNullable":
					return null;
				default:
					return this;
			}
		}
		if ( member instanceof Method ) {
			Method method = (Method) member;
			if ( method.getName().endsWith( "_JsonNullable" ) ) {
				return null;
			}
		}
		return this;
	}
}

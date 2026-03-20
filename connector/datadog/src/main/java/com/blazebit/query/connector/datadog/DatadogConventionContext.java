/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.blazebit.query.connector.base.ConventionContext;

import java.lang.reflect.Member;

/**
 * Convention context for Datadog record types. All record component accessors are included.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class DatadogConventionContext implements ConventionContext {

	public static final ConventionContext INSTANCE = new DatadogConventionContext();

	private DatadogConventionContext() {
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		return this;
	}
}

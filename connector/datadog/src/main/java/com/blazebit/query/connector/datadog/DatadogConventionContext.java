/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.blazebit.query.connector.base.ConventionContext;

import java.lang.reflect.Member;

/**
 * Convention context for Datadog record types. Returning {@code this} from
 * {@link #getSubFilter} ensures that all record component accessors are included
 * recursively, so nested Datadog record fields are also fully queryable.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
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

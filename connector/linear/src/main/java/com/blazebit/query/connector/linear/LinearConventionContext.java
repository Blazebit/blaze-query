/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.linear;

import com.blazebit.query.connector.base.ConventionContext;

import java.lang.reflect.Member;

/**
 * Convention context for Linear record types. Returning {@code this} from
 * {@link #getSubFilter} ensures nested record fields are also fully queryable.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public class LinearConventionContext implements ConventionContext {

	public static final ConventionContext INSTANCE = new LinearConventionContext();

	private LinearConventionContext() {
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		return this;
	}
}

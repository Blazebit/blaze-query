/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.observatory;

import com.blazebit.query.connector.base.ConventionContext;

import java.lang.reflect.Member;

/**
 * Convention context for {@link ObservatoryScan} mapping.
 * Adjust if you need special naming rules – or reuse a shared one.
 *
 * @author Martijn Sprengers
 * @since 1.0.25
 */
public class ObservatoryConventionContext implements ConventionContext {
	public static final ConventionContext INSTANCE = new ObservatoryConventionContext();

	private ObservatoryConventionContext() {
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		return this;
	}
}

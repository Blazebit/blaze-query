/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.defender;

import com.blazebit.query.connector.base.ConventionContext;

import java.lang.reflect.Member;

/**
 * Convention context for Defender data model records — all fields are included by default.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class DefenderConventionContext implements ConventionContext {

	public static final DefenderConventionContext INSTANCE = new DefenderConventionContext();

	private DefenderConventionContext() {
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		return this;
	}
}

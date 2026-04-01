/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.sentinel;

import com.blazebit.query.connector.base.ConventionContext;

import java.lang.reflect.Member;

/**
 * Convention context for Sentinel models, filtering internal Azure SDK housekeeping methods.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class SentinelConventionContext implements ConventionContext {

	public static final SentinelConventionContext INSTANCE = new SentinelConventionContext();

	private SentinelConventionContext() {
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		switch ( member.getName() ) {
			case "logger":
			case "innerModel":
			case "manager":
			case "serviceClient":
				return null;
			default:
				return this;
		}
	}
}

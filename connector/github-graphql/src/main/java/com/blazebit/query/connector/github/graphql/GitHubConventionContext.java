/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.blazebit.query.connector.base.ConventionContext;

import java.lang.reflect.Member;

/**
 * A method filter to exclude internal and cyclic methods from the Github models.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class GitHubConventionContext implements ConventionContext {

	public static final ConventionContext INSTANCE = new GitHubConventionContext();

	private GitHubConventionContext() {
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		return this;
	}

}

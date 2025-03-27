/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.blazebit.query.connector.base.ConventionContext;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

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
		Method method = (Method) member;
		if ( method.getExceptionTypes().length != 0 ) {
			return null;
		}
		return null;
	}

	private static class NestedUserConventionContext implements ConventionContext {

		public static final NestedUserConventionContext INSTANCE = new NestedUserConventionContext();

		private NestedUserConventionContext() {
		}

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static class NestedOrganizationConventionContext implements ConventionContext {

		public static final NestedOrganizationConventionContext INSTANCE = new NestedOrganizationConventionContext();

		private NestedOrganizationConventionContext() {
		}

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static class NestedRepositoryConventionContext implements ConventionContext {

		public static final NestedRepositoryConventionContext INSTANCE = new NestedRepositoryConventionContext();

		private NestedRepositoryConventionContext() {
		}

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}
}

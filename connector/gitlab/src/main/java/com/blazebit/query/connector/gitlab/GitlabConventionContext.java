/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.blazebit.query.connector.base.ConventionContext;
import org.gitlab4j.api.models.Project;

import java.lang.reflect.Member;

/**
 * A method filter to exclude internal and cyclic methods from the Gitlab4j models.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class GitlabConventionContext implements ConventionContext {

	public static final ConventionContext INSTANCE = new GitlabConventionContext();
	public static final String GITLAB_HOST = "https://gitlab.com";

	private GitlabConventionContext() {
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		switch ( member.getName() ) {
			case "getForkedFromProject":
				return concreteClass == Project.class ? NestedProjectConventionContext.INSTANCE : this;
			default:
				return this;
		}
	}

	private static class NestedProjectConventionContext implements ConventionContext {

		public static final NestedProjectConventionContext INSTANCE = new NestedProjectConventionContext();

		private NestedProjectConventionContext() {
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

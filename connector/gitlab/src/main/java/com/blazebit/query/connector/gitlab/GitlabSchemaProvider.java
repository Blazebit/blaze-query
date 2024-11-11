/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import java.util.Map;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;

/**
 * The schema provider for the Gitlab connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GitlabSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public GitlabSchemaProvider() {
	}

	@Override
	public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Map.<Class<?>, DataFetcher<?>>of(
				Project.class, ProjectDataFetcher.INSTANCE,
				User.class, UserDataFetcher.INSTANCE,
				Group.class, GroupDataFetcher.INSTANCE,
				ProjectMember.class, ProjectMemberDataFetcher.INSTANCE,
				GroupMember.class, GroupMemberDataFetcher.INSTANCE,
				ProjectProtectedBranch.class, ProtectedBranchDataFetcher.INSTANCE
		);
	}
}

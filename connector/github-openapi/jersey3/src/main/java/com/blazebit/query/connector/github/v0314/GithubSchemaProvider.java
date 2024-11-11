/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.v0314;

import java.util.Map;

import com.blazebit.query.connector.github.v0314.model.OrganizationSimple;
import com.blazebit.query.connector.github.v0314.model.Project;
import com.blazebit.query.connector.github.v0314.model.Repository;
import com.blazebit.query.connector.github.v0314.model.ShortBranch;
import com.blazebit.query.connector.github.v0314.model.Team;
import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

/**
 * The schema provider for the GitHub connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GithubSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public GithubSchemaProvider() {
	}

	@Override
	public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Map.<Class<?>, DataFetcher<?>>of(
				OrganizationSimple.class, OrganizationDataFetcher.INSTANCE,
				Team.class, TeamDataFetcher.INSTANCE,
				Repository.class, RepositoryDataFetcher.INSTANCE,
				ShortBranch.class, BranchDataFetcher.INSTANCE,
				Project.class, ProjectDataFetcher.INSTANCE
		);
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github;

import java.util.Map;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHProject;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;

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
				GHOrganization.class, OrganizationDataFetcher.INSTANCE,
				GHTeam.class, TeamDataFetcher.INSTANCE,
				GHRepository.class, RepositoryDataFetcher.INSTANCE,
				GHBranch.class, BranchDataFetcher.INSTANCE,
				GHProject.class, ProjectDataFetcher.INSTANCE
		);
	}
}

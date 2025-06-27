/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.6
 */
public class GitHubRepositoryDataFetcher implements DataFetcher<GitHubRepository>, Serializable {

	public static final GitHubRepositoryDataFetcher INSTANCE = new GitHubRepositoryDataFetcher();

	private GitHubRepositoryDataFetcher() {
	}

	@Override
	public List<GitHubRepository> fetch(DataFetchContext context) {
		try {
			List<GitHubGraphQlClient> githubClients = GitHubConnectorConfig.GITHUB_GRAPHQL_CLIENT.getAll(context);
			List<GitHubRepository> repositoryList = new ArrayList<>();

			for ( GitHubGraphQlClient client : githubClients) {
				repositoryList.addAll(client.fetchRepositories());
			}

			return repositoryList;
		} catch (RuntimeException e) {
			throw new DataFetcherException("Could not fetch repository list from Github GraphQL API", e);
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( GitHubRepository.class, GitHubConventionContext.INSTANCE);
	}
}

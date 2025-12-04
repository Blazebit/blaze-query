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
 * @since 1.0.7
 */
public class GitHubDefaultBranchPullRequestDataFetcher implements DataFetcher<GitHubPullRequest>, Serializable {

	public static final GitHubDefaultBranchPullRequestDataFetcher INSTANCE = new GitHubDefaultBranchPullRequestDataFetcher();

	private GitHubDefaultBranchPullRequestDataFetcher() {
	}

	@Override
	public List<GitHubPullRequest> fetch(DataFetchContext context) {
		try {
			List<GitHubGraphQlClient> githubClients = GitHubConnectorConfig.GITHUB_GRAPHQL_CLIENT.getAll(context);
			List<GitHubPullRequest> pullRequestList = new ArrayList<>();

			for (GitHubGraphQlClient client : githubClients) {
				for (GitHubRepository repository : context.getSession().getOrFetch(GitHubRepository.class)) {
					if (repository.defaultBranchRef() != null) {
						pullRequestList.addAll(
								client.fetchRepositoryPullRequests(repository.id(), repository.defaultBranchRef().name()));
					}
				}
			}

			return pullRequestList;
		} catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch pull requests list from Github GraphQL API", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( GitHubPullRequest.class, GitHubConventionContext.INSTANCE);
	}
}

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
import java.time.Duration;
import java.time.OffsetDateTime;
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
			Duration maxAge = GitHubConnectorConfig.PULL_REQUESTS_MAX_AGE.find(context);
			OffsetDateTime since = maxAge != null ? OffsetDateTime.now().minus(maxAge) : null;
			List<GitHubPullRequest> pullRequestList = new ArrayList<>();

			for (GitHubGraphQlClient client : githubClients) {
				for (GitHubRepository repository : context.getSession().getOrFetch(GitHubRepository.class)) {
					if (repository.defaultBranchRef() != null) {
						String repoId = repository.id();
						String branch = repository.defaultBranchRef().name();
						List<GitHubPullRequest> prs = since != null
								? client.fetchRepositoryPullRequestsSince(repoId, branch, since)
								: client.fetchRepositoryPullRequests(repoId, branch);
						pullRequestList.addAll(prs);
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

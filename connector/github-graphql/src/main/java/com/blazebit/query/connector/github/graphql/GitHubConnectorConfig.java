/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.blazebit.query.spi.DataFetcherConfig;

import java.time.Duration;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.6
 */
public class GitHubConnectorConfig {
	/**
	 * Specifies the {@link GitHubGraphQlClient} to use for querying data.
	 */
	public static final DataFetcherConfig<GitHubGraphQlClient> GITHUB_GRAPHQL_CLIENT =
			DataFetcherConfig.forPropertyName( "githubGraphQLClient" );

	/**
	 * Optional. When set, the pull request fetcher will only return pull requests created within
	 * this duration before the current time. For example, {@code Duration.ofDays(30)} fetches
	 * only pull requests from the last 30 days. If not set, all pull requests are returned.
	 */
	public static final DataFetcherConfig<Duration> PULL_REQUESTS_MAX_AGE =
			DataFetcherConfig.forPropertyName( "githubPullRequestsMaxAge" );

	private GitHubConnectorConfig() {
	}
}

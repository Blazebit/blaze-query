/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.blazebit.query.spi.DataFetcherConfig;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.5
 */
public class GitHubConnectorConfig {
	/**
	 * Specifies the {@link GitHubGraphQlClient} to use for querying data.
	 */
	public static final DataFetcherConfig<GitHubGraphQlClient> GITHUB_GRAPHQL_CLIENT =
			DataFetcherConfig.forPropertyName( "githubGraphQLClient" );

	private GitHubConnectorConfig() {
	}
}

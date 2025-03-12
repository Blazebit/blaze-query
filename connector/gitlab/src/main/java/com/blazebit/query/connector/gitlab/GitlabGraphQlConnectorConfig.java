/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.blazebit.query.spi.DataFetcherConfig;

/**
 * The configuration properties for the Gitlab GraphQL connector.
 *
 * @author Dimitar Prisadnikov
 * @since 1.0.4
 */
public final class GitlabGraphQlConnectorConfig {
	/**
	 * Specifies the {@link GitlabGraphQlClient} to use for querying data.
	 */
	public static final DataFetcherConfig<GitlabGraphQlClient> GITLAB_GRAPHQL_CLIENT =
			DataFetcherConfig.forPropertyName( "gitlabGraphQLClient" );

	private GitlabGraphQlConnectorConfig() {
	}
}

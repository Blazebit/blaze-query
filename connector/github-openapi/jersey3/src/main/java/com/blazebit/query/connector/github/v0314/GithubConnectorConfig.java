/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.v0314;

import com.blazebit.query.connector.github.v0314.invoker.ApiClient;
import com.blazebit.query.spi.DataFetcherConfig;

/**
 * The configuration properties for the Github connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GithubConnectorConfig {

	/**
	 * Specified the {@link ApiClient} to use for querying data.
	 */
	public static final DataFetcherConfig<ApiClient> API_CLIENT = DataFetcherConfig.forPropertyName(
			"githubApiClient" );

	private GithubConnectorConfig() {
	}
}

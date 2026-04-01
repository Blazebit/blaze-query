/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.linear;

import com.blazebit.query.spi.DataFetcherConfig;

/**
 * Configuration properties for the Linear connector.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public final class LinearConnectorConfig {

	/**
	 * Specifies the {@link LinearGraphQlClient} to use for querying Linear data.
	 * Create an instance with your Personal API Key: {@code new LinearGraphQlClient("lin_api_...")}.
	 */
	public static final DataFetcherConfig<LinearGraphQlClient> LINEAR_CLIENT =
			DataFetcherConfig.forPropertyName( "linearClient" );

	private LinearConnectorConfig() {
	}
}

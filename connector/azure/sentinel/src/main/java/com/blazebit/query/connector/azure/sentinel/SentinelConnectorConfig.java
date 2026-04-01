/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.sentinel;

import com.blazebit.query.spi.DataFetcherConfig;

/**
 * Configuration properties for the Microsoft Sentinel
 * {@link com.blazebit.query.spi.DataFetcher} instances.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public final class SentinelConnectorConfig {

	/**
	 * Specifies the {@link SentinelClientAccessor} to use for querying data.
	 */
	public static final DataFetcherConfig<SentinelClientAccessor> SENTINEL_CLIENT = DataFetcherConfig.forPropertyName(
			"sentinelClient" );

	private SentinelConnectorConfig() {
	}
}

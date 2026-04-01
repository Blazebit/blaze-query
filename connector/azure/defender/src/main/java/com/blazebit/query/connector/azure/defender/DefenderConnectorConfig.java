/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.defender;

import com.blazebit.query.spi.DataFetcherConfig;

/**
 * Configuration properties for the Microsoft Defender for Endpoint
 * {@link com.blazebit.query.spi.DataFetcher} instances.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public final class DefenderConnectorConfig {

	/**
	 * Specifies the {@link DefenderClientAccessor} to use for querying data.
	 */
	public static final DataFetcherConfig<DefenderClientAccessor> DEFENDER_CLIENT = DataFetcherConfig.forPropertyName(
			"defenderClient" );

	private DefenderConnectorConfig() {
	}
}

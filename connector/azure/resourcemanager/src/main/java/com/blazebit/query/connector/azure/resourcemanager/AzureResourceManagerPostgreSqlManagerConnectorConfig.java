/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.blazebit.query.spi.DataFetcherConfig;

/**
 * Configuration properties for the Azure {@link com.blazebit.query.spi.DataFetcher} instances.
 *
 * @author Martijn Sprengers
 * @since 1.0.3
 */
public class AzureResourceManagerPostgreSqlManagerConnectorConfig {

	public static final DataFetcherConfig<AzureResourceManagerPostgreSqlManager> POSTGRESQL_MANAGER = DataFetcherConfig.forPropertyName(
			"azureResourceManagerPostgreSqlManager" );

	private AzureResourceManagerPostgreSqlManagerConnectorConfig() {
	}
}

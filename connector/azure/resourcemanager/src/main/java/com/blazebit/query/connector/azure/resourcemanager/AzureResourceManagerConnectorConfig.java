/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.resourcegraph.ResourceGraphManager;
import com.blazebit.query.spi.DataFetcherConfig;

/**
 * Configuration properties for the Azure {@link com.blazebit.query.spi.DataFetcher} instances.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AzureResourceManagerConnectorConfig {

	/**
	 * Specified the {@link AzureResourceManager} to use for querying data.
	 */
	public static final DataFetcherConfig<AzureResourceManager> AZURE_RESOURCE_MANAGER = DataFetcherConfig.forPropertyName(
			"azureResourceManager" );

	/**
	 * Specifies the {@link ResourceGraphManager} and subscription IDs to use for Azure Resource
	 * Graph queries (e.g. patch assessment results).
	 */
	public static final DataFetcherConfig<ResourceGraphClientAccessor> RESOURCE_GRAPH_CLIENT = DataFetcherConfig.forPropertyName(
			"azureResourceGraphClient" );

	private AzureResourceManagerConnectorConfig() {
	}
}

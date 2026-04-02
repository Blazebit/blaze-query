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
	 * Specifies the {@link ResourceGraphClientAccessor} instances to use for Azure Resource Graph
	 * queries (patch assessment results, security assessments).
	 *
	 * <p>In multi-tenant setups, one {@link ResourceGraphClientAccessor} must be configured per
	 * tenant, each carrying the tenant ID, all subscription IDs for that tenant, and an
	 * authenticated {@link ResourceGraphManager}. This config is independent of
	 * {@link #AZURE_RESOURCE_MANAGER} because {@link AzureResourceManager} does not expose the
	 * credentials needed to construct a {@link ResourceGraphManager}.
	 */
	public static final DataFetcherConfig<ResourceGraphClientAccessor> RESOURCE_GRAPH_CLIENT = DataFetcherConfig.forPropertyName(
			"azureResourceGraphClient" );

	private AzureResourceManagerConnectorConfig() {
	}
}

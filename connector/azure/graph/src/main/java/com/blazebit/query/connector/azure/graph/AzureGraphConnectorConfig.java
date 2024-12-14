/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.blazebit.query.spi.DataFetcherConfig;
import com.microsoft.graph.beta.serviceclient.GraphServiceClient;

/**
 * Configuration properties for the Azure {@link com.blazebit.query.spi.DataFetcher} instances.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AzureGraphConnectorConfig {

	/**
	 * Specified the {@link GraphServiceClient} to use for querying data.
	 */
	public static final DataFetcherConfig<AzureGraphClientAccessor> GRAPH_SERVICE_CLIENT = DataFetcherConfig.forPropertyName(
			"azureGraphServiceClient" );

	private AzureGraphConnectorConfig() {
	}
}

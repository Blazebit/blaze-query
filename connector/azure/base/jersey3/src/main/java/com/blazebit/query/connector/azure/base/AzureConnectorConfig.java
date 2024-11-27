/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.base;

import com.blazebit.query.connector.azure.base.invoker.ApiClient;
import com.blazebit.query.spi.DataFetcherConfig;

/**
 * Configuration properties for the Azure {@link com.blazebit.query.spi.DataFetcher} instances.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AzureConnectorConfig {

	/**
	 * Specified the {@link ApiClient} to use for querying data.
	 */
	public static final DataFetcherConfig<ApiClient> API_CLIENT = DataFetcherConfig.forPropertyName( "azureApiClient" );

	private AzureConnectorConfig() {
	}
}

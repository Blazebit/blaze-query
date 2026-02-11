/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.devops;

import com.blazebit.query.connector.devops.invoker.ApiClient;
import com.blazebit.query.spi.DataFetcherConfig;

/**
 * The configuration properties for the Azure DevOps connector.
 *
 * @author Dimitar Prisadnikov
 * @since 1.0.8
 */
public final class DevopsConnectorConfig {

	/**
	 * Specified the {@link ApiClient} to use for querying data.
	 */
	public static final DataFetcherConfig<ApiClient> API_CLIENT = DataFetcherConfig.forPropertyName(
			"azureDevopsApiClient" );

	private DevopsConnectorConfig() {
	}
}

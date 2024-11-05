/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.kandji;

import com.blazebit.query.connector.kandji.invoker.ApiClient;
import com.blazebit.query.spi.DataFetcherConfig;

/**
 * The configuration properties for the Github connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class KandjiConnectorConfig {

	/**
	 * Specified the {@link ApiClient} to use for querying data.
	 */
	public static final DataFetcherConfig<ApiClient> API_CLIENT = DataFetcherConfig.forPropertyName(
			"kandjiApiClient" );

	private KandjiConnectorConfig() {
	}
}

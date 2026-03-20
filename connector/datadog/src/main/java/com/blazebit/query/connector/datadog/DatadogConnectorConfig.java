/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.blazebit.query.spi.DataFetcherConfig;
import com.datadog.api.client.ApiClient;

/**
 * The configuration properties for the Datadog connector.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public final class DatadogConnectorConfig {

	/**
	 * Specifies the {@link ApiClient} to use for querying Datadog data.
	 * The client must be pre-configured with the DD-API-KEY and DD-APPLICATION-KEY headers.
	 */
	public static final DataFetcherConfig<ApiClient> DATADOG_API_CLIENT =
			DataFetcherConfig.forPropertyName( "datadogApiClient" );

	/**
	 * Optional Datadog logs query filter (Lucene syntax). Defaults to {@code *} (all logs).
	 * Example: {@code "service:web* AND status:error"}
	 */
	public static final DataFetcherConfig<String> LOGS_QUERY =
			DataFetcherConfig.forPropertyName( "datadogLogsQuery" );

	/**
	 * Optional start of the logs time range. Defaults to {@code now-1h}.
	 * Accepts relative expressions such as {@code now-24h} or ISO-8601 timestamps.
	 */
	public static final DataFetcherConfig<String> LOGS_FROM =
			DataFetcherConfig.forPropertyName( "datadogLogsFrom" );

	private DatadogConnectorConfig() {
	}
}

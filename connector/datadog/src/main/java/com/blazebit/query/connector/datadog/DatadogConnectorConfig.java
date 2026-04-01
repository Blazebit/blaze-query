/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.blazebit.query.spi.DataFetcherConfig;
import com.datadog.api.client.ApiClient;

import java.time.Duration;

/**
 * The configuration properties for the Datadog connector.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
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

	/**
	 * Optional. When set, the security signals fetcher will only return signals created within
	 * this duration before the current time. Defaults to {@code Duration.ofHours(24)}.
	 */
	public static final DataFetcherConfig<Duration> SECURITY_SIGNALS_MAX_AGE =
			DataFetcherConfig.forPropertyName( "datadogSecuritySignalsMaxAge" );

	/**
	 * Optional. When set, the audit logs fetcher will only return events created within
	 * this duration before the current time. Defaults to {@code Duration.ofHours(24)}.
	 */
	public static final DataFetcherConfig<Duration> AUDIT_LOGS_MAX_AGE =
			DataFetcherConfig.forPropertyName( "datadogAuditLogsMaxAge" );

	private DatadogConnectorConfig() {
	}
}

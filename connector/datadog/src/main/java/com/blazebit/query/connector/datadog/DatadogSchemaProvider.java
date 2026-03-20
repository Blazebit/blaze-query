/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * Registers all Datadog data fetchers: logs, synthetics tests, monitors, and hosts.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public final class DatadogSchemaProvider implements QuerySchemaProvider {

	public DatadogSchemaProvider() {
	}

	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				DatadogLogDataFetcher.INSTANCE,
				DatadogSyntheticsTestDataFetcher.INSTANCE,
				DatadogMonitorDataFetcher.INSTANCE,
				DatadogHostDataFetcher.INSTANCE
		);
	}
}

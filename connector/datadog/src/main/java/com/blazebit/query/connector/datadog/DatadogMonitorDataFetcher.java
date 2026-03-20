/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.datadog.api.client.ApiClient;
import com.datadog.api.client.ApiException;
import com.datadog.api.client.v1.api.MonitorsApi;
import com.datadog.api.client.v1.api.MonitorsApi.ListMonitorsOptionalParameters;
import com.datadog.api.client.v1.model.Monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link DatadogMonitor} entries from the Datadog Monitors API (v1).
 * Monitors represent the infrastructure health checks and alert conditions.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class DatadogMonitorDataFetcher implements DataFetcher<DatadogMonitor>, Serializable {

	public static final DatadogMonitorDataFetcher INSTANCE = new DatadogMonitorDataFetcher();

	private static final int PAGE_SIZE = 1000;

	private DatadogMonitorDataFetcher() {
	}

	@Override
	public List<DatadogMonitor> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			List<DatadogMonitor> result = new ArrayList<>();
			for ( ApiClient client : clients ) {
				MonitorsApi api = new MonitorsApi( client );
				long page = 0;
				List<Monitor> batch;
				do {
					batch = api.listMonitors(
							new ListMonitorsOptionalParameters()
									.page( page )
									.pageSize( PAGE_SIZE ) );
					batch.stream().map( DatadogMonitor::from ).forEach( result::add );
					page++;
				} while ( batch.size() == PAGE_SIZE );
			}
			return result;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch Datadog monitors", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogMonitor.class, DatadogConventionContext.INSTANCE );
	}
}

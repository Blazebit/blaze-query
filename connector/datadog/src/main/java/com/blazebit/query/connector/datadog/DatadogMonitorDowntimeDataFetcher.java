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
import com.datadog.api.client.v2.api.DowntimesApi;
import com.datadog.api.client.v2.api.DowntimesApi.ListDowntimesOptionalParameters;
import com.datadog.api.client.v2.model.DowntimeResponseData;
import com.datadog.api.client.v2.model.ListDowntimesResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link DatadogMonitorDowntime} entries from the Datadog Downtimes API (v2).
 * Returns all scheduled downtimes. Active downtimes suppress monitor alerts and
 * should be reviewed for unexpectedly long or broad scopes.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class DatadogMonitorDowntimeDataFetcher implements DataFetcher<DatadogMonitorDowntime>, Serializable {

	public static final DatadogMonitorDowntimeDataFetcher INSTANCE = new DatadogMonitorDowntimeDataFetcher();

	private static final long PAGE_LIMIT = 1000L;

	private DatadogMonitorDowntimeDataFetcher() {
	}

	@Override
	public List<DatadogMonitorDowntime> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			List<DatadogMonitorDowntime> result = new ArrayList<>();
			for ( ApiClient client : clients ) {
				DowntimesApi api = new DowntimesApi( client );
				long offset = 0;
				List<DowntimeResponseData> batch;
				do {
					ListDowntimesResponse response = api.listDowntimes(
							new ListDowntimesOptionalParameters()
									.pageOffset( offset )
									.pageLimit( PAGE_LIMIT ) );
					batch = response.getData();
					if ( batch != null ) {
						batch.stream().map( DatadogMonitorDowntime::from ).forEach( result::add );
						offset += batch.size();
					}
				} while ( batch != null && batch.size() == PAGE_LIMIT );
			}
			return result;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch Datadog monitor downtimes", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogMonitorDowntime.class, DatadogConventionContext.INSTANCE );
	}
}

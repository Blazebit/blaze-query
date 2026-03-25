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
import com.datadog.api.client.v2.api.LogsApi;
import com.datadog.api.client.v2.api.LogsApi.ListLogsOptionalParameters;
import com.datadog.api.client.v2.model.Log;
import com.datadog.api.client.v2.model.LogsListRequest;
import com.datadog.api.client.v2.model.LogsListRequestPage;
import com.datadog.api.client.v2.model.LogsListResponse;
import com.datadog.api.client.v2.model.LogsQueryFilter;
import com.datadog.api.client.v2.model.LogsSort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link DatadogLog} entries from the Datadog Logs API (v2).
 * The query filter and time range are configurable via {@link DatadogConnectorConfig}.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
 */
public class DatadogLogDataFetcher implements DataFetcher<DatadogLog>, Serializable {

	public static final DatadogLogDataFetcher INSTANCE = new DatadogLogDataFetcher();

	private static final int PAGE_SIZE = 1000;

	private DatadogLogDataFetcher() {
	}

	@Override
	public List<DatadogLog> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			String query = DatadogConnectorConfig.LOGS_QUERY.find( context );
			String from = DatadogConnectorConfig.LOGS_FROM.find( context );

			if ( query == null ) {
				query = "*";
			}
			if ( from == null ) {
				from = "now-1h";
			}

			List<DatadogLog> result = new ArrayList<>();
			for ( ApiClient client : clients ) {
				LogsApi api = new LogsApi( client );
				LogsQueryFilter filter = new LogsQueryFilter()
						.from( from )
						.to( "now" )
						.query( query );

				String cursor = null;
				do {
					LogsListRequestPage page = new LogsListRequestPage().limit( PAGE_SIZE );
					if ( cursor != null ) {
						page.cursor( cursor );
					}
					LogsListRequest body = new LogsListRequest()
							.filter( filter )
							.page( page )
							.sort( LogsSort.TIMESTAMP_ASCENDING );
					LogsListResponse response = api.listLogs( new ListLogsOptionalParameters().body( body ) );

					List<Log> batch = response.getData();
					if ( batch != null ) {
						batch.stream().map( DatadogLog::from ).forEach( result::add );
					}

					cursor = null;
					if ( response.getMeta() != null
							&& response.getMeta().getPage() != null
							&& response.getMeta().getPage().getAfter() != null
							&& !response.getMeta().getPage().getAfter().isEmpty() ) {
						cursor = response.getMeta().getPage().getAfter();
					}
				} while ( cursor != null );
			}
			return result;
		}
		catch (ApiException e) {
			if ( e.getMessage() != null && e.getMessage().contains( "No valid indexes specified" ) ) {
				return List.of();
			}
			throw new DataFetcherException( "Could not fetch Datadog logs", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogLog.class, DatadogConventionContext.INSTANCE );
	}
}

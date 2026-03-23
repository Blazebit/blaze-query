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
import com.datadog.api.client.v2.api.SecurityMonitoringApi;
import com.datadog.api.client.v2.api.SecurityMonitoringApi.SearchSecurityMonitoringSignalsOptionalParameters;
import com.datadog.api.client.v2.model.SecurityMonitoringSignal;
import com.datadog.api.client.v2.model.SecurityMonitoringSignalListRequest;
import com.datadog.api.client.v2.model.SecurityMonitoringSignalListRequestFilter;
import com.datadog.api.client.v2.model.SecurityMonitoringSignalListRequestPage;
import com.datadog.api.client.v2.model.SecurityMonitoringSignalsListResponse;
import com.datadog.api.client.v2.model.SecurityMonitoringSignalsSort;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link DatadogSecuritySignal} entries from the Datadog Security Monitoring API (v2).
 * Returns signals from the last 24 hours by default.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class DatadogSecuritySignalDataFetcher implements DataFetcher<DatadogSecuritySignal>, Serializable {

	public static final DatadogSecuritySignalDataFetcher INSTANCE = new DatadogSecuritySignalDataFetcher();

	private static final int PAGE_LIMIT = 1000;

	private DatadogSecuritySignalDataFetcher() {
	}

	@Override
	public List<DatadogSecuritySignal> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			List<DatadogSecuritySignal> result = new ArrayList<>();
			OffsetDateTime now = OffsetDateTime.now();
			OffsetDateTime from = now.minusHours( 24 );
			for ( ApiClient client : clients ) {
				SecurityMonitoringApi api = new SecurityMonitoringApi( client );
				String cursor = null;
				do {
					SecurityMonitoringSignalListRequestPage page = new SecurityMonitoringSignalListRequestPage()
							.limit( PAGE_LIMIT );
					if ( cursor != null ) {
						page.cursor( cursor );
					}
					SecurityMonitoringSignalListRequest body = new SecurityMonitoringSignalListRequest()
							.filter( new SecurityMonitoringSignalListRequestFilter()
									.from( from )
									.to( now )
									.query( "*" ) )
							.page( page )
							.sort( SecurityMonitoringSignalsSort.TIMESTAMP_ASCENDING );
					SecurityMonitoringSignalsListResponse response = api.searchSecurityMonitoringSignals(
							new SearchSecurityMonitoringSignalsOptionalParameters().body( body ) );
					List<SecurityMonitoringSignal> batch = response.getData();
					if ( batch != null ) {
						batch.stream().map( DatadogSecuritySignal::from ).forEach( result::add );
					}
					cursor = null;
					if ( response.getMeta() != null
							&& response.getMeta().getPage() != null
							&& response.getMeta().getPage().getAfter() != null
							&& !response.getMeta().getPage().getAfter().isEmpty()
							&& batch != null && batch.size() == PAGE_LIMIT ) {
						cursor = response.getMeta().getPage().getAfter();
					}
				} while ( cursor != null );
			}
			return result;
		}
		catch (ApiException e) {
			if ( e.getCode() == 403 || ( e.getMessage() != null && e.getMessage().contains( "forbidden" ) ) ) {
				return List.of();
			}
			throw new DataFetcherException( "Could not fetch Datadog security signals", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogSecuritySignal.class, DatadogConventionContext.INSTANCE );
	}
}

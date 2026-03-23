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
import com.datadog.api.client.v2.api.AuditApi;
import com.datadog.api.client.v2.api.AuditApi.ListAuditLogsOptionalParameters;
import com.datadog.api.client.v2.model.AuditLogsEvent;
import com.datadog.api.client.v2.model.AuditLogsEventsResponse;
import com.datadog.api.client.v2.model.AuditLogsSort;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link DatadogAuditLog} entries from the Datadog Audit Trail API (v2).
 * Returns audit events from the last 24 hours by default.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class DatadogAuditLogDataFetcher implements DataFetcher<DatadogAuditLog>, Serializable {

	public static final DatadogAuditLogDataFetcher INSTANCE = new DatadogAuditLogDataFetcher();

	private static final int PAGE_LIMIT = 1000;

	private DatadogAuditLogDataFetcher() {
	}

	@Override
	public List<DatadogAuditLog> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			List<DatadogAuditLog> result = new ArrayList<>();
			OffsetDateTime now = OffsetDateTime.now();
			OffsetDateTime from = now.minusHours( 24 );
			for ( ApiClient client : clients ) {
				AuditApi api = new AuditApi( client );
				String cursor = null;
				do {
					ListAuditLogsOptionalParameters params = new ListAuditLogsOptionalParameters()
							.filterFrom( from )
							.filterTo( now )
							.sort( AuditLogsSort.TIMESTAMP_ASCENDING )
							.pageLimit( PAGE_LIMIT );
					if ( cursor != null ) {
						params.pageCursor( cursor );
					}
					AuditLogsEventsResponse response = api.listAuditLogs( params );
					List<AuditLogsEvent> batch = response.getData();
					if ( batch != null ) {
						batch.stream().map( DatadogAuditLog::from ).forEach( result::add );
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
			throw new DataFetcherException( "Could not fetch Datadog audit logs", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogAuditLog.class, DatadogConventionContext.INSTANCE );
	}
}

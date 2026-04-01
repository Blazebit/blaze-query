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
import com.datadog.api.client.v2.api.SecurityMonitoringApi.ListFindingsOptionalParameters;
import com.datadog.api.client.v2.model.Finding;
import com.datadog.api.client.v2.model.ListFindingsResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link DatadogCsmFinding} entries from the Datadog Cloud Security Management API (v2).
 * Findings represent misconfigurations and vulnerabilities detected across cloud resources.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
 */
public class DatadogCsmFindingDataFetcher implements DataFetcher<DatadogCsmFinding>, Serializable {

	public static final DatadogCsmFindingDataFetcher INSTANCE = new DatadogCsmFindingDataFetcher();

	private static final long PAGE_SIZE = 1000L;

	private DatadogCsmFindingDataFetcher() {
	}

	@Override
	public List<DatadogCsmFinding> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			List<DatadogCsmFinding> result = new ArrayList<>();
			for ( ApiClient client : clients ) {
				SecurityMonitoringApi api = new SecurityMonitoringApi( client );
				String cursor = null;
				do {
					ListFindingsOptionalParameters params = new ListFindingsOptionalParameters()
							.pageLimit( PAGE_SIZE );
					if ( cursor != null ) {
						params.pageCursor( cursor );
					}
					ListFindingsResponse response = api.listFindings( params );
					List<Finding> batch = response.getData();
					if ( batch != null ) {
						batch.stream().map( DatadogCsmFinding::from ).forEach( result::add );
					}
					cursor = null;
					if ( response.getMeta() != null
							&& response.getMeta().getPage() != null
							&& response.getMeta().getPage().getCursor() != null
							&& !response.getMeta().getPage().getCursor().isEmpty()
							&& batch != null && batch.size() == PAGE_SIZE ) {
						cursor = response.getMeta().getPage().getCursor();
					}
				} while ( cursor != null );
			}
			return result;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch Datadog CSM findings", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogCsmFinding.class, DatadogConventionContext.INSTANCE );
	}
}

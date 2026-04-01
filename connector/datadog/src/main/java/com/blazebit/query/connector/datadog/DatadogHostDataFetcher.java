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
import com.datadog.api.client.v1.api.HostsApi;
import com.datadog.api.client.v1.api.HostsApi.ListHostsOptionalParameters;
import com.datadog.api.client.v1.model.Host;
import com.datadog.api.client.v1.model.HostListResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link DatadogHost} entries from the Datadog Hosts API (v1).
 * Returns all infrastructure hosts that are actively reporting to Datadog.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
 */
public class DatadogHostDataFetcher implements DataFetcher<DatadogHost>, Serializable {

	public static final DatadogHostDataFetcher INSTANCE = new DatadogHostDataFetcher();

	private static final long PAGE_SIZE = 1000L;

	private DatadogHostDataFetcher() {
	}

	@Override
	public List<DatadogHost> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			List<DatadogHost> result = new ArrayList<>();
			for ( ApiClient client : clients ) {
				HostsApi api = new HostsApi( client );
				long start = 0;
				HostListResponse response;
				do {
					response = api.listHosts(
							new ListHostsOptionalParameters()
									.start( start )
									.count( PAGE_SIZE ) );
					List<Host> batch = response.getHostList();
					if ( batch != null ) {
						batch.stream().map( DatadogHost::from ).forEach( result::add );
						start += batch.size();
					}
					if ( batch == null || batch.size() < PAGE_SIZE ) {
						break;
					}
				} while ( true );
			}
			return result;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch Datadog hosts", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogHost.class, DatadogConventionContext.INSTANCE );
	}
}

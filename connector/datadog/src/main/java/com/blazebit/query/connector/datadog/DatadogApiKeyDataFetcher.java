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
import com.datadog.api.client.v2.api.KeyManagementApi;
import com.datadog.api.client.v2.api.KeyManagementApi.ListAPIKeysOptionalParameters;
import com.datadog.api.client.v2.model.APIKeysResponse;
import com.datadog.api.client.v2.model.PartialAPIKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link DatadogApiKey} entries from the Datadog Key Management API (v2).
 * Used to identify stale, unused, or overprivileged API keys.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
 */
public class DatadogApiKeyDataFetcher implements DataFetcher<DatadogApiKey>, Serializable {

	public static final DatadogApiKeyDataFetcher INSTANCE = new DatadogApiKeyDataFetcher();

	private static final long PAGE_SIZE = 100L;

	private DatadogApiKeyDataFetcher() {
	}

	@Override
	public List<DatadogApiKey> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			List<DatadogApiKey> result = new ArrayList<>();
			for ( ApiClient client : clients ) {
				KeyManagementApi api = new KeyManagementApi( client );
				long pageNumber = 0;
				List<PartialAPIKey> batch;
				do {
					APIKeysResponse response = api.listAPIKeys(
							new ListAPIKeysOptionalParameters()
									.pageSize( PAGE_SIZE )
									.pageNumber( pageNumber )
									.include( "created_by,modified_by" ) );
					batch = response.getData();
					if ( batch != null ) {
						batch.stream().map( DatadogApiKey::from ).forEach( result::add );
					}
					pageNumber++;
				} while ( batch != null && batch.size() == PAGE_SIZE );
			}
			return result;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch Datadog API keys", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogApiKey.class, DatadogConventionContext.INSTANCE );
	}
}

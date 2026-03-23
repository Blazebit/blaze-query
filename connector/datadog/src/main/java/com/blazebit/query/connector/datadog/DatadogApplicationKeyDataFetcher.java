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
import com.datadog.api.client.v2.api.KeyManagementApi.ListApplicationKeysOptionalParameters;
import com.datadog.api.client.v2.model.ListApplicationKeysResponse;
import com.datadog.api.client.v2.model.PartialApplicationKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link DatadogApplicationKey} entries from the Datadog Key Management API (v2).
 * Used to audit application keys for age, last use, and OAuth scope hygiene.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class DatadogApplicationKeyDataFetcher implements DataFetcher<DatadogApplicationKey>, Serializable {

	public static final DatadogApplicationKeyDataFetcher INSTANCE = new DatadogApplicationKeyDataFetcher();

	private static final long PAGE_SIZE = 100L;

	private DatadogApplicationKeyDataFetcher() {
	}

	@Override
	public List<DatadogApplicationKey> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			List<DatadogApplicationKey> result = new ArrayList<>();
			for ( ApiClient client : clients ) {
				KeyManagementApi api = new KeyManagementApi( client );
				long pageNumber = 0;
				List<PartialApplicationKey> batch;
				do {
					ListApplicationKeysResponse response = api.listApplicationKeys(
							new ListApplicationKeysOptionalParameters()
									.pageSize( PAGE_SIZE )
									.pageNumber( pageNumber ) );
					batch = response.getData();
					if ( batch != null ) {
						batch.stream().map( DatadogApplicationKey::from ).forEach( result::add );
					}
					pageNumber++;
				} while ( batch != null && batch.size() == PAGE_SIZE );
			}
			return result;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch Datadog application keys", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogApplicationKey.class, DatadogConventionContext.INSTANCE );
	}
}

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
import com.datadog.api.client.v2.api.UsersApi;
import com.datadog.api.client.v2.api.UsersApi.ListUsersOptionalParameters;
import com.datadog.api.client.v2.model.User;
import com.datadog.api.client.v2.model.UsersResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link DatadogUser} entries from the Datadog Users API (v2).
 * Includes MFA status, disabled state, and service account flag for compliance checks.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
 */
public class DatadogUserDataFetcher implements DataFetcher<DatadogUser>, Serializable {

	public static final DatadogUserDataFetcher INSTANCE = new DatadogUserDataFetcher();

	private static final long PAGE_SIZE = 100L;

	private DatadogUserDataFetcher() {
	}

	@Override
	public List<DatadogUser> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			List<DatadogUser> result = new ArrayList<>();
			for ( ApiClient client : clients ) {
				UsersApi api = new UsersApi( client );
				long pageNumber = 0;
				List<User> batch;
				do {
					UsersResponse response = api.listUsers(
							new ListUsersOptionalParameters()
									.pageSize( PAGE_SIZE )
									.pageNumber( pageNumber ) );
					batch = response.getData();
					if ( batch != null ) {
						batch.stream().map( DatadogUser::from ).forEach( result::add );
					}
					pageNumber++;
				} while ( batch != null && batch.size() == PAGE_SIZE );
			}
			return result;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch Datadog users", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogUser.class, DatadogConventionContext.INSTANCE );
	}
}

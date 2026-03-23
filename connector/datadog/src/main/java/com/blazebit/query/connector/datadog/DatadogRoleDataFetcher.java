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
import com.datadog.api.client.v2.api.RolesApi;
import com.datadog.api.client.v2.api.RolesApi.ListRolesOptionalParameters;
import com.datadog.api.client.v2.model.Role;
import com.datadog.api.client.v2.model.RolesResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link DatadogRole} entries from the Datadog Roles API (v2).
 * Used to audit RBAC role definitions and their user counts for least-privilege checks.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class DatadogRoleDataFetcher implements DataFetcher<DatadogRole>, Serializable {

	public static final DatadogRoleDataFetcher INSTANCE = new DatadogRoleDataFetcher();

	private static final long PAGE_SIZE = 100L;

	private DatadogRoleDataFetcher() {
	}

	@Override
	public List<DatadogRole> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			List<DatadogRole> result = new ArrayList<>();
			for ( ApiClient client : clients ) {
				RolesApi api = new RolesApi( client );
				long pageNumber = 0;
				List<Role> batch;
				do {
					RolesResponse response = api.listRoles(
							new ListRolesOptionalParameters()
									.pageSize( PAGE_SIZE )
									.pageNumber( pageNumber ) );
					batch = response.getData();
					if ( batch != null ) {
						batch.stream().map( DatadogRole::from ).forEach( result::add );
					}
					pageNumber++;
				} while ( batch != null && batch.size() == PAGE_SIZE );
			}
			return result;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch Datadog roles", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogRole.class, DatadogConventionContext.INSTANCE );
	}
}

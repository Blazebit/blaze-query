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
import com.datadog.api.client.v2.model.Permission;
import com.datadog.api.client.v2.model.PermissionsResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link DatadogPermission} entries from the Datadog Roles API (v2).
 * Returns the full RBAC permission catalog. Can be joined with role assignments
 * to determine which roles hold which permissions.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class DatadogPermissionDataFetcher implements DataFetcher<DatadogPermission>, Serializable {

	public static final DatadogPermissionDataFetcher INSTANCE = new DatadogPermissionDataFetcher();

	private DatadogPermissionDataFetcher() {
	}

	@Override
	public List<DatadogPermission> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			List<DatadogPermission> result = new ArrayList<>();
			for ( ApiClient client : clients ) {
				RolesApi api = new RolesApi( client );
				PermissionsResponse response = api.listPermissions();
				List<Permission> data = response.getData();
				if ( data != null ) {
					data.stream().map( DatadogPermission::from ).forEach( result::add );
				}
			}
			return result;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch Datadog permissions", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogPermission.class, DatadogConventionContext.INSTANCE );
	}
}

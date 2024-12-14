/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.cloud.api.PermissionsApi;
import com.blazebit.query.connector.jira.cloud.invoker.ApiClient;
import com.blazebit.query.connector.jira.cloud.invoker.ApiException;
import com.blazebit.query.connector.jira.cloud.model.UserPermission;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class PermissionDataFetcher implements DataFetcher<UserPermission>, Serializable {

	public static final PermissionDataFetcher INSTANCE = new PermissionDataFetcher();

	private PermissionDataFetcher() {
	}

	@Override
	public List<UserPermission> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraCloudConnectorConfig.API_CLIENT.getAll( context );
			List<UserPermission> list = new ArrayList<>();
			for ( ApiClient apiClient : apiClients ) {
				PermissionsApi api = new PermissionsApi( apiClient );
				Map<String, UserPermission> permissions = api.getAllPermissions().getPermissions();
				if ( permissions != null ) {
					list.addAll( permissions.values() );
				}
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch user list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( UserPermission.class, JiraCloudConventionContext.INSTANCE );
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.datacenter;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.datacenter.api.PermissionschemeApi;
import com.blazebit.query.connector.jira.datacenter.invoker.ApiClient;
import com.blazebit.query.connector.jira.datacenter.invoker.ApiException;
import com.blazebit.query.connector.jira.datacenter.model.PermissionGrantBean;
import com.blazebit.query.connector.jira.datacenter.model.PermissionSchemeBean;
import com.blazebit.query.connector.jira.datacenter.model.PermissionSchemesBean;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class PermissionDataFetcher implements DataFetcher<PermissionGrantBean>, Serializable {

	public static final PermissionDataFetcher INSTANCE = new PermissionDataFetcher();

	private PermissionDataFetcher() {
	}

	@Override
	public List<PermissionGrantBean> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraDatacenterConnectorConfig.API_CLIENT.getAll( context );
			List<PermissionGrantBean> list = new ArrayList<>();
			for ( ApiClient apiClient : apiClients ) {
				PermissionschemeApi api = new PermissionschemeApi( apiClient );
				PermissionSchemesBean permissions = api.getPermissionSchemes( null );
				if ( permissions != null && permissions.getPermissionSchemes() != null ) {
					for ( PermissionSchemeBean permissionScheme : permissions.getPermissionSchemes() ) {
						if ( permissionScheme.getPermissions() != null ) {
							list.addAll( permissionScheme.getPermissions() );
						}
					}
				}
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch permission list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( PermissionGrantBean.class, JiraDatacenterConventionContext.INSTANCE );
	}
}

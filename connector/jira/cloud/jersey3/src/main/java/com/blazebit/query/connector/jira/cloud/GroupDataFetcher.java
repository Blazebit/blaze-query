/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.cloud.api.GroupsApi;
import com.blazebit.query.connector.jira.cloud.invoker.ApiClient;
import com.blazebit.query.connector.jira.cloud.invoker.ApiException;
import com.blazebit.query.connector.jira.cloud.model.FoundGroup;
import com.blazebit.query.connector.jira.cloud.model.FoundGroups;
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
public class GroupDataFetcher implements DataFetcher<FoundGroup>, Serializable {

	public static final GroupDataFetcher INSTANCE = new GroupDataFetcher();

	private GroupDataFetcher() {
	}

	@Override
	public List<FoundGroup> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraCloudConnectorConfig.API_CLIENT.getAll( context );
			List<FoundGroup> list = new ArrayList<>();
			for ( ApiClient apiClient : apiClients ) {
				GroupsApi api = new GroupsApi( apiClient );
				FoundGroups groups = api.findGroups( null, null, null, null, null, null, null );
				if ( groups != null && groups.getGroups() != null && !groups.getGroups().isEmpty() ) {
					list.addAll( groups.getGroups() );
				}
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch member list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( FoundGroup.class, JiraCloudConventionContext.INSTANCE );
	}
}

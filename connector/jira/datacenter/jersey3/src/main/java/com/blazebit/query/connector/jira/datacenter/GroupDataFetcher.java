/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.datacenter;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.datacenter.api.GroupsApi;
import com.blazebit.query.connector.jira.datacenter.invoker.ApiClient;
import com.blazebit.query.connector.jira.datacenter.invoker.ApiException;
import com.blazebit.query.connector.jira.datacenter.model.GroupSuggestionBean;
import com.blazebit.query.connector.jira.datacenter.model.GroupSuggestionsBean;
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
public class GroupDataFetcher implements DataFetcher<GroupSuggestionBean>, Serializable {

	public static final GroupDataFetcher INSTANCE = new GroupDataFetcher();

	private GroupDataFetcher() {
	}

	@Override
	public List<GroupSuggestionBean> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraDatacenterConnectorConfig.API_CLIENT.getAll( context );
			List<GroupSuggestionBean> list = new ArrayList<>();
			for ( ApiClient apiClient : apiClients ) {
				GroupsApi api = new GroupsApi( apiClient );
				GroupSuggestionsBean groups = api.findGroups( null, null, null, null );
				if ( groups != null && groups.getGroups() != null ) {
					list.addAll( groups.getGroups() );
				}
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch group list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GroupSuggestionBean.class, JiraDatacenterConventionContext.INSTANCE );
	}
}

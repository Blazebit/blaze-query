/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.datacenter;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.datacenter.api.GroupApi;
import com.blazebit.query.connector.jira.datacenter.invoker.ApiClient;
import com.blazebit.query.connector.jira.datacenter.invoker.ApiException;
import com.blazebit.query.connector.jira.datacenter.model.GroupSuggestionBean;
import com.blazebit.query.connector.jira.datacenter.model.UserJsonBean;
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
public class MemberDataFetcher implements DataFetcher<GroupMember>, Serializable {

	public static final MemberDataFetcher INSTANCE = new MemberDataFetcher();

	private MemberDataFetcher() {
	}

	@Override
	public List<GroupMember> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraDatacenterConnectorConfig.API_CLIENT.getAll( context );
			List<GroupMember> list = new ArrayList<>();
			List<? extends GroupSuggestionBean> groups = context.getSession().getOrFetch( GroupSuggestionBean.class );
			for ( ApiClient apiClient : apiClients ) {
				GroupApi api = new GroupApi( apiClient );
				for ( GroupSuggestionBean group : groups ) {
					for ( UserJsonBean userJsonBean : api.getUsersFromGroup( group.getName(), null, null, null ) ) {
						list.add( new GroupMember( group.getName(), userJsonBean.getName() ) );
					}
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
		return DataFormats.beansConvention( GroupMember.class, JiraDatacenterConventionContext.INSTANCE );
	}
}

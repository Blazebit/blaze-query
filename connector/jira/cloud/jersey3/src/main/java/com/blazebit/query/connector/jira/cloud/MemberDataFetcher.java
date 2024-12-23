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
import com.blazebit.query.connector.jira.cloud.model.PageBeanUserDetails;
import com.blazebit.query.connector.jira.cloud.model.UserDetails;
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
			List<ApiClient> apiClients = JiraCloudConnectorConfig.API_CLIENT.getAll( context );
			List<GroupMember> list = new ArrayList<>();
			List<? extends FoundGroup> groups = context.getSession().getOrFetch( FoundGroup.class );
			for ( ApiClient apiClient : apiClients ) {
				GroupsApi api = new GroupsApi( apiClient );
				for ( FoundGroup group : groups ) {
					PageBeanUserDetails details = api.getUsersFromGroup( group.getName(), null, null, null, null );
					if ( details != null && details.getValues() != null ) {
						for ( UserDetails user : details.getValues() ) {
							list.add( new GroupMember( group.getName(), user.getName() ) );
						}
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
		return DataFormats.beansConvention( GroupMember.class, JiraCloudConventionContext.INSTANCE );
	}
}

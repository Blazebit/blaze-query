/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.datacenter;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.datacenter.api.UserApi;
import com.blazebit.query.connector.jira.datacenter.invoker.ApiClient;
import com.blazebit.query.connector.jira.datacenter.invoker.ApiException;
import com.blazebit.query.connector.jira.datacenter.model.UserBean;
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
public class UserDataFetcher implements DataFetcher<UserBean>, Serializable {

	public static final UserDataFetcher INSTANCE = new UserDataFetcher();

	private UserDataFetcher() {
	}

	@Override
	public List<UserBean> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraDatacenterConnectorConfig.API_CLIENT.getAll( context );
			List<UserBean> list = new ArrayList<>();
			for ( ApiClient apiClient : apiClients ) {
				UserApi api = new UserApi( apiClient );
				list.addAll( api.findUsers( null, Integer.MAX_VALUE, null, null, "." ) );
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch user list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( UserBean.class, JiraDatacenterConventionContext.INSTANCE );
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.cloud.api.ServerInfoApi;
import com.blazebit.query.connector.jira.cloud.invoker.ApiClient;
import com.blazebit.query.connector.jira.cloud.invoker.ApiException;
import com.blazebit.query.connector.jira.cloud.model.ServerInformation;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.9
 */
public class ServerInfoDataFetcher implements DataFetcher<ServerInformation>, Serializable {

	public static final ServerInfoDataFetcher INSTANCE = new ServerInfoDataFetcher();

	private ServerInfoDataFetcher() {
	}

	@Override
	public List<ServerInformation> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraCloudConnectorConfig.API_CLIENT.getAll( context );
			List<ServerInformation> list = new ArrayList<>();

			for ( ApiClient apiClient : apiClients ) {
				ServerInfoApi api = new ServerInfoApi( apiClient );
				list.add( api.getServerInfo() );
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch server information list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( ServerInformation.class, JiraCloudConventionContext.INSTANCE );
	}
}

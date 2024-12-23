/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.datacenter;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.datacenter.api.ProjectApi;
import com.blazebit.query.connector.jira.datacenter.invoker.ApiClient;
import com.blazebit.query.connector.jira.datacenter.invoker.ApiException;
import com.blazebit.query.connector.jira.datacenter.model.ProjectBean;
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
public class ProjectDataFetcher implements DataFetcher<ProjectBean>, Serializable {

	public static final ProjectDataFetcher INSTANCE = new ProjectDataFetcher();

	private ProjectDataFetcher() {
	}

	@Override
	public List<ProjectBean> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraDatacenterConnectorConfig.API_CLIENT.getAll( context );
			List<ProjectBean> list = new ArrayList<>();
			for ( ApiClient apiClient : apiClients ) {
				ProjectApi api = new ProjectApi( apiClient );
				list.addAll( api.getAllProjects( null, null, null, null ) );
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch project list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( ProjectBean.class, JiraDatacenterConventionContext.INSTANCE );
	}
}

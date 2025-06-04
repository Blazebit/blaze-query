/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.cloud.api.IssueSearchApi;
import com.blazebit.query.connector.jira.cloud.invoker.ApiClient;
import com.blazebit.query.connector.jira.cloud.invoker.ApiException;
import com.blazebit.query.connector.jira.cloud.model.SearchAndReconcileResults;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.8
 */
public class InProgressIssueDataFetcher implements DataFetcher<SearchAndReconcileResults>, Serializable {

	public static final InProgressIssueDataFetcher INSTANCE = new InProgressIssueDataFetcher();

	private InProgressIssueDataFetcher() {
	}

	@Override
	public List<SearchAndReconcileResults> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraCloudConnectorConfig.API_CLIENT.getAll( context );

			List<SearchAndReconcileResults> list = new ArrayList<>();
			for ( ApiClient apiClient : apiClients ) {
				IssueSearchApi api = new IssueSearchApi( apiClient );

				SearchAndReconcileResults result = api.searchAndReconsileIssuesUsingJql("statusCategory != Done", null, null, List.of("*all"), null, null, null, null, null);
				list.add(result);
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch issue list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( SearchAndReconcileResults.class, JiraCloudConventionContext.INSTANCE );
	}
}

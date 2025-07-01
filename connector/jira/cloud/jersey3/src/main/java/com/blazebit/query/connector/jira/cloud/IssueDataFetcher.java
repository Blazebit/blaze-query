/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.cloud.api.IssueSearchApi;
import com.blazebit.query.connector.jira.cloud.invoker.ApiClient;
import com.blazebit.query.connector.jira.cloud.invoker.ApiException;
import com.blazebit.query.connector.jira.cloud.model.IssueBean;
import com.blazebit.query.connector.jira.cloud.model.SearchAndReconcileResults;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.8
 */
public class IssueDataFetcher implements DataFetcher<IssueBeanWrapper>, Serializable {

	public static final IssueDataFetcher INSTANCE = new IssueDataFetcher();

	private IssueDataFetcher() {
	}

	@Override
	public List<IssueBeanWrapper> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraCloudConnectorConfig.API_CLIENT.getAll(context);
			String query = JiraCloudConnectorConfig.JQL_QUERY.get(context);

			List<IssueBean> issuesList = new ArrayList<>();
			for (ApiClient apiClient : apiClients) {
				IssueSearchApi api = new IssueSearchApi(apiClient);
				issuesList.addAll( fetchAllIssuesWithPagination(api, query));
			}

			// Convert IssueBean instances to IssueBeanWrapper instances
			return issuesList.stream()
					.map(issueBean -> {
						try {
							return new IssueBeanWrapper(issueBean);
						}
						catch (URISyntaxException e) {
							throw new RuntimeException( e );
						}
					})
					.collect(Collectors.toList());

		}
		catch (ApiException e) {
			throw new DataFetcherException("Could not fetch issue list", e);
		}
	}

	private List<IssueBean> fetchAllIssuesWithPagination(IssueSearchApi api, String query) throws ApiException {
		String nextPageToken = null;
		boolean hasMoreResults = true;
		List<IssueBean> list = new ArrayList<>();

		// searchAndReconsileIssuesUsingJql requires a bounded query
		String jqlQuery = query != null ? query : "statusCategory != Done";

		while (hasMoreResults) {
			SearchAndReconcileResults result = api.searchAndReconsileIssuesUsingJql(
					jqlQuery,
					nextPageToken,
					null,
					List.of("*all"),
					null,
					null,
					null,
					null,
					null
			);

			if (result.getIssues() != null) {
				list.addAll(result.getIssues());
			}

			if (result.getNextPageToken() == null) {
				hasMoreResults = false;
			}
			else {
				nextPageToken = result.getNextPageToken();
			}
		}

		return list;
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention(IssueBeanWrapper.class, JiraCloudConventionContext.INSTANCE);
	}
}

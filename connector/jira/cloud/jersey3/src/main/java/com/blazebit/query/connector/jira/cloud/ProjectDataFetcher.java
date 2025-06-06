/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.jira.cloud.api.ProjectsApi;
import com.blazebit.query.connector.jira.cloud.invoker.ApiClient;
import com.blazebit.query.connector.jira.cloud.invoker.ApiException;
import com.blazebit.query.connector.jira.cloud.invoker.ApiResponse;
import com.blazebit.query.connector.jira.cloud.model.PageBeanProject;
import com.blazebit.query.connector.jira.cloud.model.Project;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ProjectDataFetcher implements DataFetcher<Project>, Serializable {

	public static final ProjectDataFetcher INSTANCE = new ProjectDataFetcher();

	private ProjectDataFetcher() {
	}

	@Override
	public List<Project> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = JiraCloudConnectorConfig.API_CLIENT.getAll( context );
			List<Project> list = new ArrayList<>();
			for ( ApiClient apiClient : apiClients ) {
				ProjectsApi api = new ProjectsApi( apiClient );
				list.addAll( fetchAllProjectsWithPagination(api) );
			}
			return list;
		} catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch project list", e );
		}
	}


	private List<Project> fetchAllProjectsWithPagination(ProjectsApi api) throws ApiException {
		List<Project> allProjects = new ArrayList<>();
		Long startAt = 0L;
		boolean hasMoreResults = true;

		while (hasMoreResults) {
			ApiResponse<PageBeanProject> response = api.searchProjectsWithHttpInfo(
					startAt,
					null, // 50 by default
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					List.of("live", "archived"),
					null,
					null
			);

			PageBeanProject page = response.getData();
			if (page.getValues() != null) {
				allProjects.addAll(page.getValues());
			}

			Long total = page.getTotal();
			if (total == null || startAt + Objects.requireNonNull( page.getValues() ).size() >= total) {
				hasMoreResults = false;
			} else {
				startAt += page.getMaxResults();
			}
		}

		return allProjects;
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( Project.class, JiraCloudConventionContext.INSTANCE );
	}
}

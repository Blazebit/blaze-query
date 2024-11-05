/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.v0314;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.github.v0314.api.ProjectsApi;
import com.blazebit.query.connector.github.v0314.invoker.ApiClient;
import com.blazebit.query.connector.github.v0314.invoker.ApiException;
import com.blazebit.query.connector.github.v0314.model.OrganizationSimple;
import com.blazebit.query.connector.github.v0314.model.Project;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

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
			List<ApiClient> apiClients = GithubConnectorConfig.API_CLIENT.getAll( context );
			List<Project> list = new ArrayList<>();
			Set<Integer> seenProjects = new HashSet<>();
			List<? extends OrganizationSimple> organizations = context.getSession()
					.getOrFetch( OrganizationSimple.class );
			for ( ApiClient apiClient : apiClients ) {
				ProjectsApi projectsApi = new ProjectsApi( apiClient );
				for ( OrganizationSimple organization : organizations ) {
					for ( int page = 1; ; page++ ) {
						List<Project> projects = projectsApi.projectsListForOrg(
								organization.getLogin(),
								null,
								100,
								page
						);
						for ( Project project : projects ) {
							if ( seenProjects.add( project.getId() ) ) {
								list.add( project );
							}
						}
						if ( projects.size() != 100 ) {
							break;
						}
					}
				}

			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch project list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( Project.class, GithubConventionContext.INSTANCE );
	}
}

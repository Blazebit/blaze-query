/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
public class GraphQlProjectDataFetcher implements DataFetcher<GitlabProject>, Serializable {

	public static final GraphQlProjectDataFetcher INSTANCE = new GraphQlProjectDataFetcher();

	private GraphQlProjectDataFetcher() {
	}

	@Override
	public List<GitlabProject> fetch(DataFetchContext context) {
		try {
			List<GitlabGraphQlClient> gitlabClients = GitlabConnectorConfig.GITLAB_GRAPHQL_CLIENT.getAll( context );
			List<GitlabProject> projectList = new ArrayList<>();

			for ( GitlabGraphQlClient client : gitlabClients ) {
				projectList.addAll( client.fetchProjects( true ) );
			}

			return projectList;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch project list from GitLab GraphQL API", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( GitlabProject.class, GitlabConventionContext.INSTANCE );
	}
}

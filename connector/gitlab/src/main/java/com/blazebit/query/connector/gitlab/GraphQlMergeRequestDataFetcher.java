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
 * @author Dimitar Prisadnikov
 * @since 1.0.10
 */
public class GraphQlMergeRequestDataFetcher implements DataFetcher<GitlabMergeRequest>, Serializable {

	public static final GraphQlMergeRequestDataFetcher INSTANCE = new GraphQlMergeRequestDataFetcher();

	private GraphQlMergeRequestDataFetcher() {
	}

	@Override
	public List<GitlabMergeRequest> fetch(DataFetchContext context) {
		try {
			List<GitlabGraphQlClient> gitlabClients = GitlabGraphQlConnectorConfig.GITLAB_GRAPHQL_CLIENT.getAll( context );
			List<GitlabMergeRequest> mergeRequestList = new ArrayList<>();

			for ( GitlabGraphQlClient client : gitlabClients ) {
				mergeRequestList.addAll( client.fetchMergeRequestsFromProjects( GitlabMergeRequestState.all ) );
			}

			return mergeRequestList;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch merge request list from GitLab GraphQL API", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( GitlabMergeRequest.class );
	}
}

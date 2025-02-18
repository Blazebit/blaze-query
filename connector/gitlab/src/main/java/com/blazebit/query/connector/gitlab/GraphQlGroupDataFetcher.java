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

public class GraphQlGroupDataFetcher implements DataFetcher<GitlabGroup>, Serializable {

	public static final GraphQlGroupDataFetcher INSTANCE = new GraphQlGroupDataFetcher();

	private GraphQlGroupDataFetcher() {
	}

	@Override
	public List<GitlabGroup> fetch(DataFetchContext context) {
		try {
			List<GitlabGraphQlClient> gitlabClients = GitlabConnectorConfig.GITLAB_GRAPHQL_CLIENT.getAll(context);
			List<GitlabGroup> groupList = new ArrayList<>();

			for ( GitlabGraphQlClient client : gitlabClients) {
				groupList.addAll(client.fetchGroups(true));
			}

			return groupList;
		} catch (RuntimeException e) {
			throw new DataFetcherException("Could not fetch group list from GitLab GraphQL API", e);
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention(GitlabGroup.class, GitlabConventionContext.INSTANCE);
	}
}

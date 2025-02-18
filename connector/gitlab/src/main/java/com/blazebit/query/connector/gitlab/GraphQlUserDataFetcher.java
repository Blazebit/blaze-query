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

public class GraphQlUserDataFetcher implements DataFetcher<GitlabUser>, Serializable {

	public static final GraphQlUserDataFetcher INSTANCE = new GraphQlUserDataFetcher();

	private GraphQlUserDataFetcher() {
	}

	@Override
	public List<GitlabUser> fetch(DataFetchContext context) {
		try {
			List<GitlabGraphQlClient> gitlabClients = GitlabConnectorConfig.GITLAB_GRAPHQL_CLIENT.getAll(context);
			List<GitlabUser> userList = new ArrayList<>();

			for ( GitlabGraphQlClient client : gitlabClients) {
				userList.addAll(client.fetchUsers(List.of("gid://gitlab/User/21369228")));
			}

			return userList;
		} catch (RuntimeException e) {
			throw new DataFetcherException("Could not fetch user list from GitLab GraphQL API", e);
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention(GitlabUser.class);
	}
}

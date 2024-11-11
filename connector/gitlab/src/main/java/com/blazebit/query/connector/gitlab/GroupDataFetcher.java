/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Group;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class GroupDataFetcher implements DataFetcher<Group>, Serializable {

	public static final GroupDataFetcher INSTANCE = new GroupDataFetcher();

	private GroupDataFetcher() {
	}

	@Override
	public List<Group> fetch(DataFetchContext context) {
		try {
			List<GitLabApi> gitlabApis = GitlabConnectorConfig.GITLAB_API.getAll( context );
			List<Group> list = new ArrayList<>();
			for ( GitLabApi gitLabApi : gitlabApis ) {
				list.addAll( gitLabApi.getGroupApi().getGroups() );
			}
			return list;
		}
		catch (GitLabApiException | RuntimeException e) {
			throw new DataFetcherException( "Could not fetch group list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( Group.class, GitlabConventionContext.INSTANCE );
	}
}

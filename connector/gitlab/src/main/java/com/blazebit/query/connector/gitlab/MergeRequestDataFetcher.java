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
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.10
 */
public class MergeRequestDataFetcher implements DataFetcher<MergeRequest>, Serializable {

	public static final MergeRequestDataFetcher INSTANCE = new MergeRequestDataFetcher();

	private MergeRequestDataFetcher() {
	}

	@Override
	public List<MergeRequest> fetch(DataFetchContext context) {
		try {
			List<GitLabApi> gitlabApis = GitlabConnectorConfig.GITLAB_API.getAll( context );
			List<MergeRequest> list = new ArrayList<>();

			for ( GitLabApi gitLabApi : gitlabApis ) {
				for ( Project project : context.getSession().getOrFetch(Project.class)) {
					list.addAll( gitLabApi.getMergeRequestApi().getMergeRequests( project.getId() ));
				}
			}
			return list;
		}
		catch (GitLabApiException | RuntimeException e) {
			throw new DataFetcherException( "Could not fetch merge request list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( MergeRequest.class, GitlabConventionContext.INSTANCE );
	}
}

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
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.ProtectedBranch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ProtectedBranchDataFetcher implements DataFetcher<ProjectProtectedBranch>, Serializable {

	public static final ProtectedBranchDataFetcher INSTANCE = new ProtectedBranchDataFetcher();

	private ProtectedBranchDataFetcher() {
	}

	@Override
	public List<ProjectProtectedBranch> fetch(DataFetchContext context) {
		try {
			List<GitLabApi> gitlabApis = GitlabConnectorConfig.GITLAB_API.getAll( context );
			List<ProjectProtectedBranch> list = new ArrayList<>();
			for ( GitLabApi gitLabApi : gitlabApis ) {
				for ( Project project : context.getSession().getOrFetch( Project.class ) ) {
					for ( ProtectedBranch protectedBranch : gitLabApi.getProtectedBranchesApi()
							.getProtectedBranches( project.getId() ) ) {
						list.add( new ProjectProtectedBranch( protectedBranch, project ) );
					}
				}
			}
			return list;
		}
		catch (GitLabApiException | RuntimeException e) {
			throw new DataFetcherException( "Could not fetch protected branch list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( ProjectProtectedBranch.class, GitlabConventionContext.INSTANCE );
	}
}

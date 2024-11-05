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
import org.gitlab4j.api.models.Project;

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
			List<GitLabApi> gitlabApis = GitlabConnectorConfig.GITLAB_API.getAll( context );
			List<Project> list = new ArrayList<>();
			for ( GitLabApi gitLabApi : gitlabApis ) {
				if ( gitLabApi.getGitLabServerUrl().startsWith( GitlabConventionContext.GITLAB_HOST ) ) {
					list.addAll( gitLabApi.getProjectApi().getMemberProjects() );
				}
				else {
					list.addAll( gitLabApi.getProjectApi().getProjects() );
				}
			}
			return list;
		}
		catch (GitLabApiException | RuntimeException e) {
			throw new DataFetcherException( "Could not fetch project list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( Project.class, GitlabConventionContext.INSTANCE );
	}
}

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
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ProjectMemberDataFetcher implements DataFetcher<ProjectMember>, Serializable {

	public static final ProjectMemberDataFetcher INSTANCE = new ProjectMemberDataFetcher();

	private ProjectMemberDataFetcher() {
	}

	@Override
	public List<ProjectMember> fetch(DataFetchContext context) {
		try {
			List<GitLabApi> gitlabApis = GitlabConnectorConfig.GITLAB_API.getAll( context );
			List<ProjectMember> list = new ArrayList<>();
			for ( GitLabApi gitLabApi : gitlabApis ) {
				for ( Project project : context.getSession().getOrFetch( Project.class ) ) {
					for ( Member member : gitLabApi.getProjectApi().getMembers( project.getId() ) ) {
						list.add( new ProjectMember( member, project ) );
					}
				}
			}
			return list;
		}
		catch (GitLabApiException | RuntimeException e) {
			throw new DataFetcherException( "Could not fetch project member list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( ProjectMember.class, GitlabConventionContext.INSTANCE );
	}
}

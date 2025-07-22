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
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches a list of GitLab projects where the current user is a member
 *
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
			List<GitLabApi> gitlabApis = GitlabConnectorConfig.GITLAB_API.getAll(context);
			List<Project> list = new ArrayList<>();
			for (GitLabApi gitLabApi : gitlabApis) {
				// Only get projects where the current user is a member of
				// Otherwise all GitLab projects are returned
				Pager<Project> pager = gitLabApi.getProjectApi().getProjects(
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						true,
						null,
						null,
						100
						);
				fetchPaginated(pager, list);
			}
			return list;
		}
		catch (GitLabApiException | RuntimeException e) {
			throw new DataFetcherException("Could not fetch project list", e);
		}
	}

	private <T> void fetchPaginated(Pager<T> pager, List<T> resultList) {
		while (pager.hasNext()) {
			List<T> page = pager.next();
			resultList.addAll(page);
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( Project.class, GitlabConventionContext.INSTANCE );
	}
}

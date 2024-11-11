/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.User;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class UserDataFetcher implements DataFetcher<User>, Serializable {

	public static final UserDataFetcher INSTANCE = new UserDataFetcher();

	private UserDataFetcher() {
	}

	@Override
	public List<User> fetch(DataFetchContext context) {
		try {
			List<GitLabApi> gitlabApis = GitlabConnectorConfig.GITLAB_API.getAll( context );
			List<User> list = new ArrayList<>();
			for ( GitLabApi gitLabApi : gitlabApis ) {
				if ( gitLabApi.getGitLabServerUrl().startsWith( GitlabConventionContext.GITLAB_HOST ) ) {
					Set<Long> memberIds = new HashSet<>();
					for ( ProjectMember member : context.getSession().getOrFetch( ProjectMember.class ) ) {
						if ( memberIds.add( member.getId() ) ) {
							list.add( gitLabApi.getUserApi().getUser( member.getId() ) );
						}
					}
					for ( GroupMember member : context.getSession().getOrFetch( GroupMember.class ) ) {
						if ( memberIds.add( member.getId() ) ) {
							list.add( gitLabApi.getUserApi().getUser( member.getId() ) );
						}
					}
				}
				else {
					list.addAll( gitLabApi.getUserApi().getUsers() );
				}
			}
			return list;
		}
		catch (GitLabApiException | RuntimeException e) {
			throw new DataFetcherException( "Could not fetch user list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( User.class, GitlabConventionContext.INSTANCE );
	}
}

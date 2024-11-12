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
import org.gitlab4j.api.models.Member;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class GroupMemberDataFetcher implements DataFetcher<GroupMember>, Serializable {

	public static final GroupMemberDataFetcher INSTANCE = new GroupMemberDataFetcher();

	private GroupMemberDataFetcher() {
	}

	@Override
	public List<GroupMember> fetch(DataFetchContext context) {
		try {
			List<GitLabApi> gitlabApis = GitlabConnectorConfig.GITLAB_API.getAll( context );
			List<GroupMember> list = new ArrayList<>();
			for ( GitLabApi gitLabApi : gitlabApis ) {
				for ( Group group : context.getSession().getOrFetch( Group.class ) ) {
					for ( Member member : gitLabApi.getGroupApi().getMembers( group.getId() ) ) {
						list.add( new GroupMember( member, group ) );
					}
				}
			}
			return list;
		}
		catch (GitLabApiException | RuntimeException e) {
			throw new DataFetcherException( "Could not fetch group member list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GroupMember.class, GitlabConventionContext.INSTANCE );
	}
}

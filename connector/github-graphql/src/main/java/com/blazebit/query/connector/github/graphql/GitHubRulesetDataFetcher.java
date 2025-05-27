/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.7
 */
public class GitHubRulesetDataFetcher implements DataFetcher<GitHubRuleset >, Serializable {

	public static final GitHubRulesetDataFetcher INSTANCE = new GitHubRulesetDataFetcher();

	private GitHubRulesetDataFetcher() {
	}

	@Override
	public List<GitHubRuleset > fetch(DataFetchContext context) {
		try {
			List<GitHubGraphQlClient> githubClients = GitHubConnectorConfig.GITHUB_GRAPHQL_CLIENT.getAll(context);
			List<GitHubRuleset > rulesetList = new ArrayList<>();

			for (GitHubGraphQlClient client : githubClients) {
				for (GitHubRepository repository : context.getSession().getOrFetch(GitHubRepository.class)) {
					rulesetList.addAll(client.fetchRepositoryRulesets(repository.id()));
				}
			}

			return rulesetList;
		} catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch rulesets list from Github GraphQL API", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( GitHubRuleset .class, GitHubConventionContext.INSTANCE);
	}
}

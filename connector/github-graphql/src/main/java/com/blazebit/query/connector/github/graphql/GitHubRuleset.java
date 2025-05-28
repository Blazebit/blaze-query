/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * This record maps to GitHub's {@code RepositoryRuleset} GraphQL model with additional context fields.
 * <p>
 * In GitHub's GraphQL API, a {@code RepositoryRuleset} can be associated with one of three source types:
 * {@code Repository}, {@code Organization}, or {@code Enterprise}. Each of these source types has a unique
 * identifier. This class captures these source relationships through the {@code repositoryId},
 * {@code organizationId}, and {@code enterpriseId} fields.
 *
 * @author Dimitar Prisadnikov
 * @since 1.0.6
 */
public record GitHubRuleset(
		String id,
		String target,
		String enforcement,
		GitHubRulesetCondition conditions,
		String repositoryId,
		String organizationId,
		String enterpriseId,
		List<GitHubRule> rules
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubRuleset fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			String repositoryId = null;
			String organizationId = null;
			String enterpriseId = null;

			JsonNode sourceNode = json.path("source");
			if (!sourceNode.isMissingNode()) {
				String typeName = sourceNode.path("__typename").asText();
				String id = sourceNode.path("id").asText();

				switch (typeName) {
					case "Repository" -> repositoryId = id;
					case "Organization" -> organizationId = id;
					case "Enterprise" -> enterpriseId = id;
				}
			}

			return new GitHubRuleset(
					json.path("id").asText(),
					json.path("target").asText(),
					json.path("enforcement").asText(),
					GitHubRulesetCondition.parseRulesetConditions(json.path("conditions")),
					repositoryId,
					organizationId,
					enterpriseId,
					GitHubRule.parseRules(json.path("rules"))
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GithubRuleset", e);
		}
	}
}

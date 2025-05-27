/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * This record maps to GitHub's {@code RepositoryRuleset} model with additional context fields. The original GitHub
 * one does not contain references to its parent repository or organization, making it difficult to trace back to
 * its origin. Unlike other GitHub objects such as {@code BranchProtectionRule} which includes a repository object,
 * {@code RepositoryRuleset} lack this contextual information.
 * <p>
 * To address this limitation, this implementation adds {@code repositoryId} and {@code organizationId}
 * fields, which store the parent context information when a ruleset is retrieved. These fields
 * are populated during deserialization when the parent context is known.
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
		List<GitHubRule> rules
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubRuleset fromJson(String jsonString, String repositoryId, String organizationId) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			return new GitHubRuleset(
					json.path("id").asText(),
					json.path("target").asText(),
					json.path("enforcement").asText(),
					GitHubRulesetCondition.parseRulesetConditions(json.path("conditions")),
					repositoryId,
					organizationId,
					GitHubRule.parseRules(json.path("rules"))
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GithubRuleset", e);
		}
	}
}

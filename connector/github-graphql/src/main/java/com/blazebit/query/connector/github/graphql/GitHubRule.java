/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.6
 */
public record GitHubRule(
		String type,
		GitHubRulePullRequestParameters pullRequestParameters,
		GitHubRuleRequiredStatusChecksParameters requiredStatusChecksParameters,
		GitHubRuleCopilotCodeReviewParameters copilotCodeReviewParameters
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubRule fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);
			String ruleType = json.path("type").asText();

			return new GitHubRule(
					ruleType,
					GitHubRulePullRequestParameters.parseRuleParameters( json.path( "parameters" ) ),
					GitHubRuleRequiredStatusChecksParameters.parseRequiredStatusChecksParameters( json.path( "parameters" ) ),
					GitHubRuleCopilotCodeReviewParameters.parseCopilotCodeReviewParameters( json.path( "parameters" ) )
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GithubRule", e);
		}
	}

	public static List<GitHubRule> parseRules(JsonNode json) {
		JsonNode nodesArray = json.path("nodes");
		if (nodesArray.isMissingNode() || !nodesArray.isArray() || nodesArray.isEmpty()) {
			return List.of();
		}
		return StreamSupport.stream(nodesArray.spliterator(), false)
				.map(node -> GitHubRule.fromJson(node.toString()))
				.collect( Collectors.toList());
	}
}

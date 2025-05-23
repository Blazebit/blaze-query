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
public record GitHubRuleset(
		String target,
		String enforcement,
		GitHubRulesetCondition conditions,
		List<GitHubRule> rules
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubRuleset fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			return new GitHubRuleset(
					json.path("target").asText(),
					json.path("enforcement").asText(),
					GitHubRulesetCondition.parseRulesetConditions(json.path("conditions")),
					GitHubRule.parseRules(json.path("rules"))
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GithubRuleset", e);
		}
	}

	public static List<GitHubRuleset> parseRulesets(JsonNode json) {
		JsonNode nodesArray = json.path("nodes");
		if (nodesArray.isMissingNode() || !nodesArray.isArray() || nodesArray.isEmpty()) {
			return List.of();
		}
		return StreamSupport.stream(nodesArray.spliterator(), false)
				.map(node -> GitHubRuleset.fromJson(node.toString()))
				.collect(Collectors.toList());
	}
}

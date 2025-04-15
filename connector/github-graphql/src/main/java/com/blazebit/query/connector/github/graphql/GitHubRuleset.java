/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.blazebit.query.connector.utils.ObjectMappers;
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
		RulesetConditions conditions,
		List<GitHubRule> rules
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubRuleset fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			return new GitHubRuleset(
					json.path("target").asText(),
					json.path("enforcement").asText(),
					parseRulesetConditions(json.path("conditions")),
					parseRules(json.path("rules"))
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GithubRuleset", e);
		}
	}

	private static RulesetConditions parseRulesetConditions(JsonNode json) {
		if (json.isMissingNode() || json.isNull()) {
			return null;
		}
		return new RulesetConditions(
				parseRefNameIncludes(json.path("refName").path("include"))
		);
	}

	private static List<String> parseRefNameIncludes(JsonNode json) {
		if (json.isMissingNode() || !json.isArray() || json.isEmpty()) {
			return List.of();
		}
		return StreamSupport.stream(json.spliterator(), false)
				.map(JsonNode::asText)
				.collect(Collectors.toList());
	}

	private static List<GitHubRule> parseRules(JsonNode json) {
		JsonNode nodesArray = json.path("nodes");
		if (nodesArray.isMissingNode() || !nodesArray.isArray() || nodesArray.isEmpty()) {
			return List.of();
		}
		return StreamSupport.stream(nodesArray.spliterator(), false)
				.map(node -> GitHubRule.fromJson(node.toString()))
				.collect(Collectors.toList());
	}

	public record RulesetConditions(List<String> refNameIncludes) {}
}

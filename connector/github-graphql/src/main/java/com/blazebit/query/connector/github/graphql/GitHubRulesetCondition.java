/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public record GitHubRulesetCondition(List<String> refNameIncludes) {
	public static GitHubRulesetCondition parseRulesetConditions(JsonNode json) {
		if (json.isMissingNode() || json.isNull()) {
			return null;
		}
		return new GitHubRulesetCondition(
				parseRefNameIncludes(json.path("refName").path("include"))
		);
	}

	private static List<String> parseRefNameIncludes(JsonNode json) {
		if (json.isMissingNode() || !json.isArray() || json.isEmpty()) {
			return List.of();
		}
		return StreamSupport.stream(json.spliterator(), false)
				.map(JsonNode::asText)
				.collect( Collectors.toList());
	}
}

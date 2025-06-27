/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.7
 */
public record GitHubBranchProtectionRuleMatchingRef(String id, String name) {
	public static List<GitHubBranchProtectionRuleMatchingRef> parseMatchingRefs(JsonNode json) {
		JsonNode nodesArray = json.path("nodes");
		if (nodesArray.isMissingNode() || !nodesArray.isArray() || nodesArray.isEmpty()) {
			return List.of();
		}
		return StreamSupport.stream(nodesArray.spliterator(), false)
				.map(node -> new GitHubBranchProtectionRuleMatchingRef(
						node.path("id").asText(),
						node.path("name").asText()
				))
				.collect( Collectors.toList());
	}
}

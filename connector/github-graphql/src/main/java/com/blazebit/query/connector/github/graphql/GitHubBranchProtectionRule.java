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
 * @since 1.0.5
 */
public record GitHubBranchProtectionRule(
		String id,
		boolean allowsForcePushes,
		boolean requiresCodeOwnerReviews,
		List<MatchingRef> matchingRefs
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubBranchProtectionRule fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			return new GitHubBranchProtectionRule(
					json.path("id").asText(),
					json.path("allowsForcePushes").asBoolean(false),
					json.path("requiresCodeOwnerReviews").asBoolean(false),
					parseMatchingRefs(json.path("matchingRefs"))
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GithubBranchProtectionRule", e);
		}
	}

	private static List<MatchingRef> parseMatchingRefs(JsonNode json) {
		JsonNode nodesArray = json.path("nodes");
		if (nodesArray.isMissingNode() || !nodesArray.isArray() || nodesArray.isEmpty()) {
			return List.of();
		}
		return StreamSupport.stream(nodesArray.spliterator(), false)
				.map(node -> new MatchingRef(
						node.path("id").asText(),
						node.path("name").asText()
				))
				.collect(Collectors.toList());
	}

	public record MatchingRef(String id, String name) {}
}

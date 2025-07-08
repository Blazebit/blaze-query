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
public record GitHubRulesetCondition(
		List<String> refNameIncludes,
		List<String> refNameExcludes,
		List<String> repositoryIds,
		List<String> repositoryNameIncludes,
		List<String> repositoryNameExcludes
) {
	public static GitHubRulesetCondition parseRulesetConditions(JsonNode json) {
		if (json.isMissingNode() || json.isNull()) {
			return null;
		}
		return new GitHubRulesetCondition(
				parseStringList(json.path("refName").path("include")),
				parseStringList(json.path("refName").path("exclude")),
				parseStringList(json.path("repositoryId").path("repositoryIds")),
				parseStringList(json.path("repositoryName").path("include")),
				parseStringList(json.path("repositoryName").path("exclude"))
		);
	}

	private static List<String> parseStringList(JsonNode json) {
		if (json.isMissingNode() || !json.isArray() || json.isEmpty()) {
			return List.of();
		}
		return StreamSupport.stream(json.spliterator(), false)
				.map(JsonNode::asText)
				.collect(Collectors.toList());
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.blazebit.query.connector.gitlab.Util.ISO_DATE_FORMAT;
import static com.blazebit.query.connector.gitlab.Util.parseDate;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
public record GitlabProject(
		String id,
		String name,
		Boolean archived,
		String avatarUrl,
		Date createdAt,
		String description,
		Date lastActivityAt,
		String path,
		Date updatedAt,
		String groupId,
		String defaultBranch, // repository.rootRef
		List<GitlabBranchRule> branchRules
) {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static GitlabProject fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			return new GitlabProject(
					json.get("id").asText(),
					json.get("name").asText(),
					json.path("archived").asBoolean(false),
					json.path("avatarUrl").asText(null),
					parseDate(json.path("createdAt"), ISO_DATE_FORMAT),
					json.path("description").asText(null),
					parseDate(json.path("lastActivityAt"), ISO_DATE_FORMAT),
					json.path("path").asText(null),
					parseDate(json.path("updatedAt"), ISO_DATE_FORMAT),
					json.has("group") ? json.get("group").path("id").asText(null) : null,
					json.has("repository") ? json.get("repository").path("rootRef").asText(null) : null,
					parseBranchRules(json.path("branchRules"))
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GitlabProject", e);
		}
	}

	private static List<GitlabBranchRule> parseBranchRules(JsonNode json) {
		if (!json.has("edges")) {
			return List.of();
		}
		return StreamSupport.stream(json.get("edges").spliterator(), false)
				.map(edge -> GitlabBranchRule.fromJson(edge.path("node").toString()))
				.collect(Collectors.toList());
	}
}

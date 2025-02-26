/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
	public static GitlabProject fromJson(JSONObject json) {
		return new GitlabProject(
				json.getString("id"),
				json.getString("name"),
				json.optBoolean("archived", false),
				json.optString("avatarUrl", null),
				parseDate(json.optString("createdAt", null)),
				json.optString("description", null),
				parseDate(json.optString("lastActivityAt", null)),
				json.optString("path", null),
				parseDate(json.optString("updatedAt", null)),
				json.optJSONObject("group") != null ? json.optJSONObject("group").optString("id", null) : null,
				json.optJSONObject("repository") != null ? json.optJSONObject("repository").optString("rootRef", null) : null,
				parseBranchRules(json.optJSONObject("branchRules"))
		);
	}

	private static List<GitlabBranchRule> parseBranchRules(JSONObject json) {
		if (json == null || !json.has("edges")) {
			return List.of();
		}
		JSONArray edges = json.optJSONArray("edges");
		return IntStream.range(0, edges.length())
				.mapToObj(i -> GitlabBranchRule.fromJson(edges.getJSONObject(i).optJSONObject("node")))
				.collect(Collectors.toList());
	}

	private static Date parseDate(String dateString) {
		if (dateString == null || dateString.isEmpty()) {
			return null; // Return null if no date is provided
		}
		try {
			return new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(dateString);
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse date: " + dateString, e);
		}
	}
}

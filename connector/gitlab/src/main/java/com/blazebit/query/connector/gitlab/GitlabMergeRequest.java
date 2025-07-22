/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.12
 */
public record GitlabMergeRequest(
		String id,
		String name,
		String title,
		boolean approved,
		GitlabMergeRequestState state,
		int approvalsRequired,
		OffsetDateTime createdAt,
		OffsetDateTime mergedAt,
		String targetBranch,
		String targetProjectId,
		String authorId,
		List<String> approvedByIds
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitlabMergeRequest fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			// Extract approvedBy IDs from the nested structure
			List<String> approvedByIds = new ArrayList<>();
			JsonNode approvedByNodes = json.path("approvedBy").path("nodes");
			if (approvedByNodes.isArray()) {
				for (JsonNode node : approvedByNodes) {
					approvedByIds.add(node.path("id").asText());
				}
			}

			// Get author ID directly
			String authorId = json.path("author").path("id").asText();

			// Parse date fields
			OffsetDateTime createdAt = null;
			if (!json.path("createdAt").isMissingNode() && !json.path("createdAt").isNull()) {
				createdAt = OffsetDateTime.parse(json.get("createdAt").asText());
			}

			OffsetDateTime mergedAt = null;
			if (!json.path("mergedAt").isMissingNode() && !json.path("mergedAt").isNull()) {
				mergedAt = OffsetDateTime.parse(json.get("mergedAt").asText());
			}

			return new GitlabMergeRequest(
					json.get("id").asText(),
					json.get("name").asText(),
					json.path("title").asText(""),
					json.path("approved").asBoolean(false),
					GitlabMergeRequestState.valueOf(json.get("state").asText()),
					json.path("approvalsRequired").asInt(0),
					createdAt,
					mergedAt,
					json.path("targetBranch").asText(""),
					json.path("targetProjectId").asText(""),
					authorId,
					approvedByIds
			);
		}
		catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GitlabMergeRequest", e);
		}
	}
}

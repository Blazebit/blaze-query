/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import static com.blazebit.query.connector.github.graphql.DateUtils.parseIsoOffsetDateTime;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.7
 */
public record GitHubPullRequest(
		String id,
		String title,
		OffsetDateTime createdAt,
		boolean closed,
		OffsetDateTime closedAt,
		boolean merged,
		OffsetDateTime mergedAt,
		State state,
		ReviewDecision reviewDecision,
		Ref baseRef
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubPullRequest fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			return new GitHubPullRequest(
					json.path("id").asText(),
					json.path( "title").asText(),
					parseIsoOffsetDateTime(json.path("createdAt").asText()),
					json.path("closed").asBoolean(false),
					parseIsoOffsetDateTime(json.path("closedAt").asText()),
					json.path("merged").asBoolean(false),
					parseIsoOffsetDateTime(json.path("mergedAt").asText()),
					State.valueOf(json.path("state").asText().toUpperCase()),
					ReviewDecision.valueOf(json.path("reviewDecision").asText().toUpperCase()),
					parseRef(json.path("baseRef"))
					);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GithubBranchProtectionRule", e);
		}
	}

	private static Ref parseRef(JsonNode json) {
		if (json.isMissingNode() || json.isNull()) {
			return null;
		}
		return new Ref(
				json.path("id").asText(),
				json.path("name").asText()
		);
	}

	public record Ref(String id, String name) {}

	public enum State {
		OPEN,
		CLOSED,
		MERGED
	}

	public enum ReviewDecision {
		CHANGES_REQUESTED,
		APPROVED,
		REVIEW_REQUIRED,
	}
}

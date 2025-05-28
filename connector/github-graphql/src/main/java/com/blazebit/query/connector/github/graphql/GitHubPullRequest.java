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
		GitHubPullRequestState state,
		GitHubPullRequestReviewDecision reviewDecision,
		String repositoryId,
		GitHubBranchRef baseRef
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubPullRequest fromJson(String jsonString, String repositoryId) {
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
					GitHubPullRequestState.valueOf(json.path("state").asText().toUpperCase()),
					json.path("reviewDecision").isNull() || json.path("reviewDecision").asText().isEmpty()
							? null
							: GitHubPullRequestReviewDecision.valueOf(json.path("reviewDecision").asText().toUpperCase()),
					repositoryId,
					GitHubBranchRef.parseBranchRef(json.path("baseRef"))
					);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GithubBranchProtectionRule", e);
		}
	}
}

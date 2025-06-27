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
 * @since 1.0.6
 */
public record GitHubRepository(
		String id,
		String name,
		String description,
		boolean isArchived,
		boolean isDisabled,
		boolean isInOrganization,
		boolean isEmpty,
		boolean isPrivate,
		boolean isFork,
		boolean forkingAllowed,
		GitHubRepositoryVisibility visibility,
		OffsetDateTime createdAt,
		GitHubRef defaultBranchRef,
		GitHubRepositoryOwner owner
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubRepository fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			return new GitHubRepository(
					json.get("id").asText(),
					json.get("name").asText(),
					json.path("description").asText(null),
					json.path("isArchived").asBoolean(false),
					json.path("isDisabled").asBoolean(false),
					json.path("isInOrganization").asBoolean(false),
					json.path("isEmpty").asBoolean(false),
					json.path("isPrivate").asBoolean(false),
					json.path("isFork").asBoolean(false),
					json.path("forkingAllowed").asBoolean(false),
					GitHubRepositoryVisibility.valueOf(json.path("visibility").asText().toUpperCase()),
					parseIsoOffsetDateTime(json.path("createdAt").asText()),
					GitHubRef.parseBranchRef(json.path("defaultBranchRef")),
					GitHubRepositoryOwner.parseOwner(json.path("owner"))
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GraphQlRepository", e);
		}
	}
}

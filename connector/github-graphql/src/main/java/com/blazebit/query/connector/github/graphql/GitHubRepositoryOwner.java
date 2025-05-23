/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.7
 */
public record GitHubRepositoryOwner(String id, String login, GitHubRepositoryOwnerType type) {
	public static GitHubRepositoryOwner parseOwner(JsonNode json) {
		if (json.isMissingNode() || json.isNull()) {
			return null;
		}
		return new GitHubRepositoryOwner(
				json.path("id").asText(),
				json.path("login").asText(),
				GitHubRepositoryOwnerType.valueOf(json.path("__typename").asText().toUpperCase())
		);
	}
}

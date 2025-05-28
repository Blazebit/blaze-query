/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * Represents a Git reference in the GitHub GraphQL API.
 *
 * <p>{@code GitHubRef} encapsulates a reference to a Git object in GitHub's system,
 * such as a branch or tag. In the GitHub GraphQL API, a {@code Ref} object contains multiple fields,
 * but this implementation focuses on the {@code id} and {@code name}</p>
 *
 * <p>This class is used throughout the GitHub connector to represent branch references
 * in various contexts such as pull requests (baseRef, headRef), repositories (defaultBranchRef),
 * and when querying branch information directly.</p>
 *
 * @author Dimitar Prisadnikov
 * @since 1.0.7
 * @see <a href="https://docs.github.com/en/graphql/reference/objects#ref">GitHub GraphQL API Reference</a>
 */

public record GitHubRef(String id, String name) {
	public static GitHubRef parseBranchRef(JsonNode json) {
		if (json.isMissingNode() || json.isNull()) {
			return null;
		}
		return new GitHubRef(
				json.path("id").asText(),
				json.path("name").asText()
		);
	}
}

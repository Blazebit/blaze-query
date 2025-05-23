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
public record GitHubRepositoryMinimal(String id, String name){
	public static GitHubRepositoryMinimal parseRepositoryMinimal(JsonNode json) {
		if (json.isMissingNode() || json.isNull()) {
			return null;
		}
		return new GitHubRepositoryMinimal(
				json.path("id").asText(),
				json.path("name").asText()
		);
	}
}

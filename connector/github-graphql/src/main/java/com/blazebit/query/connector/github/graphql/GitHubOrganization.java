/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.blazebit.query.connector.utils.ObjectMappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.6
 */
public record GitHubOrganization(String id, String name, Boolean requiresTwoFactorAuthentication) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubOrganization fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			return new GitHubOrganization(
					json.get("id").asText(),
					json.get("name").asText(),
					json.path("requiresTwoFactorAuthentication").asBoolean(false)
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GraphQlRepository", e);
		}
	}
}

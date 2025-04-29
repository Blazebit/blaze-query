/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static com.blazebit.query.connector.github.graphql.GitHubRepository.parseRulesets;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.6
 */
public record GitHubOrganization(String id, String name, Boolean requiresTwoFactorAuthentication, List<GitHubRuleset> rulesets) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubOrganization fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			return new GitHubOrganization(
					json.get("id").asText(),
					json.get("name").asText(),
					json.path("requiresTwoFactorAuthentication").asBoolean(false),
					parseRulesets(json.path("rulesets"))

					);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GraphQlRepository", e);
		}
	}

}

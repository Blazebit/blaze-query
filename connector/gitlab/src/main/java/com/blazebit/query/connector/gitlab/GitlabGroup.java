/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;

import static com.blazebit.query.connector.gitlab.Util.ISO_DATE_FORMAT;
import static com.blazebit.query.connector.gitlab.Util.parseDate;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitlabGroup(String id, String name, String path, boolean requireTwoFactorAuthentication, int twoFactorGracePeriod, Date createdAt, String description, String fullName, String projectCreationLevel,  Date updatedAt, String visibility) {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static GitlabGroup fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			return new GitlabGroup(
					json.get("id").asText(),
					json.get("name").asText(),
					json.get("path").asText(),
					json.path("requireTwoFactorAuthentication").asBoolean(false),
					json.path("twoFactorGracePeriod").asInt(0),
					parseDate(json.path("createdAt"), ISO_DATE_FORMAT),
					json.path("description").asText(null),
					json.path("fullName").asText(null),
					json.path("projectCreationLevel").asText(null),
					parseDate(json.path("updatedAt"), ISO_DATE_FORMAT),
					json.path("visibility").asText(null)
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GitlabGroup", e);
		}
	}
}

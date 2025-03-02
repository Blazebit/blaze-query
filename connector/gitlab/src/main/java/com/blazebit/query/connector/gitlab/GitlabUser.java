/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;

import static com.blazebit.query.connector.gitlab.Util.DATE_FORMAT;
import static com.blazebit.query.connector.gitlab.Util.ISO_DATE_FORMAT;
import static com.blazebit.query.connector.gitlab.Util.parseDate;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitlabUser(
		String id,
		String name,
		String username,
		Date lastActivityOn,
		boolean active,
		String avatarUrl,
		String bio,
		boolean bot,
		String commitEmail,
		Date createdAt,
		String discord,
		boolean gitpodEnabled,
		Integer groupCount,
		boolean human,
		String jobTitle,
		String linkedin,
		String location,
		String organization,
		String pronouns,
		String publicEmail,
		String twitter,
		String webPath,
		String webUrl
) {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static GitlabUser fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			return new GitlabUser(
					json.get("id").asText(),
					json.get("name").asText(),
					json.get("username").asText(),
					parseDate(json.path("lastActivityOn"), DATE_FORMAT),
					json.get("active").asBoolean(),
					json.has("avatarUrl") ? json.get("avatarUrl").asText() : null,
					json.has("bio") ? json.get("bio").asText() : null,
					json.get("bot").asBoolean(),
					json.has("commitEmail") ? json.get("commitEmail").asText() : null,
					parseDate(json.path("createdAt"),ISO_DATE_FORMAT),
					json.has("discord") ? json.get("discord").asText() : null,
					json.has("gitpodEnabled") && json.get("gitpodEnabled").asBoolean(false),
					json.has("groupCount") ? json.get("groupCount").asInt(0) : 0,
					json.has("human") && json.get("human").asBoolean(false),
					json.has("jobTitle") ? json.get("jobTitle").asText() : null,
					json.has("linkedin") ? json.get("linkedin").asText() : null,
					json.has("location") ? json.get("location").asText() : null,
					json.has("organization") ? json.get("organization").asText() : null,
					json.has("pronouns") ? json.get("pronouns").asText() : null,
					json.has("publicEmail") ? json.get("publicEmail").asText() : null,
					json.has("twitter") ? json.get("twitter").asText() : null,
					json.has("webPath") ? json.get("webPath").asText() : null,
					json.has("webUrl") ? json.get("webUrl").asText() : null
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GitlabUser", e);
		}
	}

}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import org.json.JSONObject;

public record GitlabUser(
		String id,
		String name,
		String username,
		String lastActivityOn,
		boolean active,
		String avatarUrl,
		String bio,
		String bot,
		String commitEmail,
		String createdAt,
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
	public static GitlabUser fromJson(JSONObject json) {
		return new GitlabUser(
				json.getString("id"),
				json.getString("name"),
				json.getString("username"),
				json.optString("lastActivityOn", null),
				json.getBoolean("active"),
				json.optString("avatarUrl", null),
				json.optString("bio", null),
				json.optString("bot", null),
				json.optString("commitEmail", null),
				json.optString("createdAt", null),
				json.optString("discord", null),
				json.optBoolean("gitpodEnabled", false),
				json.optInt("groupCount", 0),
				json.optBoolean("human", false),
				json.optString("jobTitle", null),
				json.optString("linkedin", null),
				json.optString("location", null),
				json.optString("organization", null),
				json.optString("pronouns", null),
				json.optString("publicEmail", null),
				json.optString("twitter", null),
				json.optString("webPath", null),
				json.optString("webUrl", null)
		);
	}
}

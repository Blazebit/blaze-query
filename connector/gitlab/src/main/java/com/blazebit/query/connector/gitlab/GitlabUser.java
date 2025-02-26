/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import org.json.JSONObject;

import java.util.Date;

import static com.blazebit.query.connector.gitlab.Util.DATE_FORMAT;
import static com.blazebit.query.connector.gitlab.Util.ISO_DATE_FORMAT;
import static com.blazebit.query.connector.gitlab.Util.parseDate;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
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
	public static GitlabUser fromJson(JSONObject json) {
		return new GitlabUser(
				json.getString("id"),
				json.getString("name"),
				json.getString("username"),
				parseDate( json.optString("lastActivityOn", null), DATE_FORMAT),
				json.getBoolean("active"),
				json.optString("avatarUrl", null),
				json.optString("bio", null),
				json.getBoolean("bot"),
				json.optString("commitEmail", null),
				parseDate( json.optString("createdAt", null), ISO_DATE_FORMAT),
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

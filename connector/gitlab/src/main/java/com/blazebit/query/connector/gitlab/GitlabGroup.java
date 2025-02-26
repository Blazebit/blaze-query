/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import org.json.JSONObject;

import java.util.Date;

import static com.blazebit.query.connector.gitlab.Util.ISO_DATE_FORMAT;
import static com.blazebit.query.connector.gitlab.Util.parseDate;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
public record GitlabGroup(String id, String name, String path, boolean requireTwoFactorAuthentication, int twoFactorGracePeriod, Date createdAt, String description, String fullName, String projectCreationLevel,  Date updatedAt, String visibility) {
	public static GitlabGroup fromJson(JSONObject json) {
		return new GitlabGroup(
				json.getString("id"),
				json.getString("name"),
				json.getString("path"),
				json.getBoolean("requireTwoFactorAuthentication"),
				json.getInt("twoFactorGracePeriod"),
				parseDate(json.optString("createdAt", null), ISO_DATE_FORMAT),
				json.optString("description", null),
				json.optString("fullName", null),
				json.optString("projectCreationLevel", null),
				parseDate(json.optString("updatedAt", null), ISO_DATE_FORMAT),
				json.optString("visibility", null)
		);
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import org.json.JSONObject;

public record GitlabUser(String id, String name, String username, String lastActivityOn, boolean active) {
	public static GitlabUser fromJson(JSONObject json) {
		return new GitlabUser(
				json.getString("id"),
				json.getString("name"),
				json.getString("username"),
				json.optString("lastActivityOn", null),
				json.getBoolean("active")
		);
	}
}

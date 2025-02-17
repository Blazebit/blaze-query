/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import org.json.JSONObject;

public record GitlabProject(String id, String name, String defaultBranch) {
	public static GitlabProject fromJson(JSONObject json) {
		return new GitlabProject(
				json.getString("id"),
				json.getString("name"),
				json.optString("defaultBranch", null)
		);
	}
}

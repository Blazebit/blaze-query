/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import org.json.JSONObject;

public record GitlabGroup(String id, String name, String path, boolean requireTwoFactorAuthentication, int twoFactorGracePeriod) {
	public static GitlabGroup fromJson(JSONObject json) {
		return new GitlabGroup(
				json.getString("id"),
				json.getString("name"),
				json.getString("path"),
				json.getBoolean("requireTwoFactorAuthentication"),
				json.getInt("twoFactorGracePeriod")
		);
	}
}

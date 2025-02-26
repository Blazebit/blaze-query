/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import org.json.JSONObject;

/**
 * @author Martijn Sprengers
 * @since 1.0.4
 */
public record GitlabBranchRule(
		String id,
		String name,
		Boolean isDefault,
		Boolean isProtected,
		Boolean allowForcePush,
		Boolean codeOwnerApprovalRequired
) {
	public static GitlabBranchRule fromJson(JSONObject json) {
		JSONObject branchProtection = json.optJSONObject("branchProtection");

		return new GitlabBranchRule(
				json.getString("id"),
				json.getString("name"),
				json.optBoolean("isDefault", false),
				json.optBoolean("isProtected", false),
				branchProtection != null && branchProtection.optBoolean("allowForcePush", false),
				branchProtection != null && branchProtection.optBoolean("codeOwnerApprovalRequired", false)
		);
	}
}

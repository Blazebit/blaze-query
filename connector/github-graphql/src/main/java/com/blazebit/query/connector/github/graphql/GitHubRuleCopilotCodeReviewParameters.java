/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Donghwi Kim
 * @since 1.0.28
 */
public record GitHubRuleCopilotCodeReviewParameters(
		boolean reviewDraftPullRequests,
		boolean reviewOnPush
) {
	public static GitHubRuleCopilotCodeReviewParameters parseCopilotCodeReviewParameters(JsonNode json) {
		if (json.isMissingNode() || json.isNull()) {
			return null;
		}

		return new GitHubRuleCopilotCodeReviewParameters(
				json.path("reviewDraftPullRequests").asBoolean(false),
				json.path("reviewOnPush").asBoolean(false)
		);
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.blazebit.query.connector.utils.ObjectMappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.6
 */
public record GitHubRule(
		String type,
		RuleParameters parameters
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubRule fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			return new GitHubRule(
					json.path("type").asText(),
					parseRuleParameters(json.path("parameters"))
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GithubRule", e);
		}
	}

	private static RuleParameters parseRuleParameters(JsonNode json) {
		if (json.isMissingNode() || json.isNull()) {
			return null;
		}
		return new RuleParameters(
				json.path("requireCodeOwnerReview").asBoolean(false),
				json.path("requiredApprovingReviewCount").asInt(0),
				json.path("automaticCopilotCodeReviewEnabled").asBoolean(false),
				json.path("dismissStaleReviewsOnPush").asBoolean(false),
				json.path("requireLastPushApproval").asBoolean(false),
				json.path("requiredReviewThreadResolution").asBoolean(false),
				json.path("strictRequiredStatusChecksPolicy").asBoolean(false)
		);
	}

	public record RuleParameters(
			boolean requireCodeOwnerReview,
			int requiredApprovingReviewCount,
			boolean automaticCopilotCodeReviewEnabled,
			boolean dismissStaleReviewsOnPush,
			boolean requireLastPushApproval,
			boolean requiredReviewThreadResolution,
			boolean strictRequiredStatusChecksPolicy

	) {}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.7
 */
public record GitHubPullRequestParameters(
		boolean requireCodeOwnerReview,
		int requiredApprovingReviewCount,
		boolean automaticCopilotCodeReviewEnabled,
		boolean dismissStaleReviewsOnPush,
		boolean requireLastPushApproval,
		boolean requiredReviewThreadResolution
) {
	/**
	 * <p>GitHub's GraphQL API exposes rulesets with a mix of parameterized and non-parameterized types.
	 * For example, rules like {@code PULL_REQUEST} expose detailed boolean parameters such as:
	 *
	 * <ul>
	 *   <li>{@code requireCodeOwnerReview}</li>
	 *   <li>{@code dismissStaleReviewsOnPush}</li>
	 *   <li>{@code requiredReviewThreadResolution}</li>
	 * </ul>
	 *
	 * <p>However, other rules like {@code REQUIRED_SIGNATURES} or {@code NON_FAST_FORWARD} appear only by type name
	 * if enabled and are omitted entirely if disabled. These rules do not include parameters.
	 */
	public static GitHubPullRequestParameters parseRuleParameters(JsonNode json) {
		if (json.isMissingNode() || json.isNull()) {
			return null;
		}

		return new GitHubPullRequestParameters(
				json.path("requireCodeOwnerReview").asBoolean(false),
				json.path("requiredApprovingReviewCount").asInt(0),
				json.path("automaticCopilotCodeReviewEnabled").asBoolean(false),
				json.path("dismissStaleReviewsOnPush").asBoolean(false),
				json.path("requireLastPushApproval").asBoolean(false),
				json.path("requiredReviewThreadResolution").asBoolean(false)
		);
	}
}

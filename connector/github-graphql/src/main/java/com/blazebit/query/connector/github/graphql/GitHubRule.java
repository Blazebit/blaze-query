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
		PullRequestParameters pullRequestParameters,
		RequiredStatusChecksParameters requiredStatusChecksParameters
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubRule fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);
			String ruleType = json.path("type").asText();

			return new GitHubRule(
					ruleType,
					parseRuleParameters( json.path( "parameters" ) ),
					parseRequiredStatusChecksParameters( json.path( "parameters" ) )
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GithubRule", e);
		}
	}

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
	private static PullRequestParameters parseRuleParameters(JsonNode json) {
		if (json.isMissingNode() || json.isNull()) {
			return null;
		}

		return new PullRequestParameters(
				json.path("requireCodeOwnerReview").asBoolean(false),
				json.path("requiredApprovingReviewCount").asInt(0),
				json.path("automaticCopilotCodeReviewEnabled").asBoolean(false),
				json.path("dismissStaleReviewsOnPush").asBoolean(false),
				json.path("requireLastPushApproval").asBoolean(false),
				json.path("requiredReviewThreadResolution").asBoolean(false)
		);
	}

	private static RequiredStatusChecksParameters parseRequiredStatusChecksParameters(JsonNode json) {
		if (json.isMissingNode() || json.isNull()) {
			return null;
		}

		return new RequiredStatusChecksParameters(
				json.path("strictRequiredStatusChecksPolicy").asBoolean(false)
		);
	}

	public record PullRequestParameters(
			boolean requireCodeOwnerReview,
			int requiredApprovingReviewCount,
			boolean automaticCopilotCodeReviewEnabled,
			boolean dismissStaleReviewsOnPush,
			boolean requireLastPushApproval,
			boolean requiredReviewThreadResolution
	) {
	}

	public record RequiredStatusChecksParameters(
			boolean strictRequiredStatusChecksPolicy
	) {
	}
}

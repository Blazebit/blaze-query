/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.6
 */
public record GitHubBranchProtectionRule(
		String id,
		boolean allowsForcePushes,
		boolean requiresCodeOwnerReviews,
		boolean allowsDeletions,
		boolean isAdminEnforced,
		boolean requireLastPushApproval,
		int requiredApprovingReviewCount,
		boolean requiresConversationResolution,
		boolean restrictsReviewDismissals,
		boolean requiresCommitSignatures,
		boolean requiresStatusChecks,
		boolean requiresStrictStatusChecks,
		boolean dismissesStaleReviews,
		boolean requiresApprovingReviews,
		List<GitHubBranchProtectionRuleMatchingRef> matchingRefs
) {
	private static final ObjectMapper MAPPER = ObjectMappers.getInstance();

	public static GitHubBranchProtectionRule fromJson(String jsonString) {
		try {
			JsonNode json = MAPPER.readTree(jsonString);

			return new GitHubBranchProtectionRule(
					json.path("id").asText(),
					json.path("allowsForcePushes").asBoolean(false),
					json.path("requiresCodeOwnerReviews").asBoolean(false),
					json.path("allowsDeletions").asBoolean(false),
					json.path("isAdminEnforced").asBoolean(false),
					json.path("requireLastPushApproval").asBoolean(false),
					json.path("requiredApprovingReviewCount").asInt(0),
					json.path("requiresConversationResolution").asBoolean(false),
					json.path("restrictsReviewDismissals").asBoolean(false),
					json.path("requiresCommitSignatures").asBoolean(false),
					json.path("requiresStatusChecks").asBoolean(false),
					json.path("requiresStrictStatusChecks").asBoolean(false),
					json.path("dismissesStaleReviews").asBoolean(false),
					json.path("requiresApprovingReviews").asBoolean(false),
					GitHubBranchProtectionRuleMatchingRef.parseMatchingRefs(json.path("matchingRefs"))
			);
		} catch (Exception e) {
			throw new RuntimeException("Error parsing JSON for GithubBranchProtectionRule", e);
		}
	}
}

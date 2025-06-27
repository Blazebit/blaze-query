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
public record GitHubRuleRequiredStatusChecksParameters(boolean strictRequiredStatusChecksPolicy) {
	public static GitHubRuleRequiredStatusChecksParameters parseRequiredStatusChecksParameters(JsonNode json) {
		if (json.isMissingNode() || json.isNull()) {
			return null;
		}

		return new GitHubRuleRequiredStatusChecksParameters(
				json.path("strictRequiredStatusChecksPolicy").asBoolean(false)
		);
	}
}

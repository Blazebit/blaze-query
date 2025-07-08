/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.7
 */
public enum GitHubPullRequestReviewDecision {
	CHANGES_REQUESTED,
	APPROVED,
	REVIEW_REQUIRED,
}

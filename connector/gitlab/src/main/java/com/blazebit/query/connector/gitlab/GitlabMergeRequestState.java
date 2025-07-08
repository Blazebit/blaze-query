/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.10
 */
public enum GitlabMergeRequestState {
	merged,
	opened,
	closed,
	locked,
	all
}

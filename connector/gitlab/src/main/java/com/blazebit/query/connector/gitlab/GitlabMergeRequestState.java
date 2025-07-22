/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.12
 */
public enum GitlabMergeRequestState {
	merged,
	opened,
	closed,
	locked,
	all
}

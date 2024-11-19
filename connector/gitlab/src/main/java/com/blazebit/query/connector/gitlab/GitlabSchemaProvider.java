/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the Gitlab connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GitlabSchemaProvider implements QuerySchemaProvider {
	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				ProjectDataFetcher.INSTANCE,
				UserDataFetcher.INSTANCE,
				GroupDataFetcher.INSTANCE,
				ProjectMemberDataFetcher.INSTANCE,
				GroupMemberDataFetcher.INSTANCE,
				ProtectedBranchDataFetcher.INSTANCE
		);
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.v0314;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the GitHub connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GithubSchemaProvider implements QuerySchemaProvider {
	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				OrganizationDataFetcher.INSTANCE,
				TeamDataFetcher.INSTANCE,
				RepositoryDataFetcher.INSTANCE,
				BranchDataFetcher.INSTANCE,
				ProjectDataFetcher.INSTANCE
		);
	}
}

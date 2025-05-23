/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.graphql;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.6
 */
public final class GitHubSchemaProvider implements QuerySchemaProvider {
	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				GitHubRepositoryDataFetcher.INSTANCE,
				GitHubOrganizationDataFetcher.INSTANCE
		);
	}
}

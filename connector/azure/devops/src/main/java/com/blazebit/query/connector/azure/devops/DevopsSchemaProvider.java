/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.devops;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the Azure DevOps connector.
 *
 * @author Dimitar Prisadnikov
 * @since 1.0.8
 */
public final class DevopsSchemaProvider implements QuerySchemaProvider {
	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				AccountDataFetcher.INSTANCE,
				RepositoryDataFetcher.INSTANCE,
				PolicyConfigurationDataFetcher.INSTANCE,
				WorkItemDataFetcher.INSTANCE
		);
	}
}

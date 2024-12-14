/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.base;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the GCP ResourceManager connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GcpResourceManagerSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public GcpResourceManagerSchemaProvider() {
	}

	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				OrganizationDataFetcher.INSTANCE,
				FolderDataFetcher.INSTANCE,
				AssetDataFetcher.INSTANCE,
				ProjectDataFetcher.INSTANCE
		);
	}
}

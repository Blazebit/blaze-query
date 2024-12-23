/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.iam;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the GCP IAM connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GcpIamSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public GcpIamSchemaProvider() {
	}

	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				ServiceAccountDataFetcher.INSTANCE,
				RoleDataFetcher.INSTANCE,
				PolicyDataFetcher.INSTANCE
		);
	}
}

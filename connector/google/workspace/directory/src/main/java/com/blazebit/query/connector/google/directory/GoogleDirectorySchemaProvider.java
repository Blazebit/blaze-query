/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.directory;

import java.util.Set;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

/**
 * The schema provider for the Google Workspace connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GoogleDirectorySchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public GoogleDirectorySchemaProvider() {
	}

	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				UserDataFetcher.INSTANCE,
				GroupDataFetcher.INSTANCE,
				MemberDataFetcher.INSTANCE
		);
	}
}

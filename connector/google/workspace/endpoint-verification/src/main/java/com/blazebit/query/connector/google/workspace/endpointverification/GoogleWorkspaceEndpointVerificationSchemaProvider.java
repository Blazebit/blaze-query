/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.workspace.endpointverification;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the Google Workspace Endpoint Verification connector.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public final class GoogleWorkspaceEndpointVerificationSchemaProvider implements QuerySchemaProvider {

	/**
	 * Creates a new schema provider.
	 */
	public GoogleWorkspaceEndpointVerificationSchemaProvider() {
	}

	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				MobileDeviceDataFetcher.INSTANCE,
				ChromeOsDeviceDataFetcher.INSTANCE
		);
	}
}

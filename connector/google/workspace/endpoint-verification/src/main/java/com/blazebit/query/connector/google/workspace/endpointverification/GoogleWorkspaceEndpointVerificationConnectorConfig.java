/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.workspace.endpointverification;

import com.blazebit.query.spi.DataFetcherConfig;
import com.google.api.services.directory.Directory;

/**
 * The configuration properties for the Google Workspace Endpoint Verification connector.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public final class GoogleWorkspaceEndpointVerificationConnectorConfig {

	/**
	 * Specifies the {@link Directory} to use for querying endpoint verification data.
	 * Uses the same property name as the Google Workspace Directory connector so a
	 * single configured service instance is shared between both connectors.
	 */
	public static final DataFetcherConfig<Directory> GOOGLE_DIRECTORY_SERVICE =
			DataFetcherConfig.forPropertyName( "googleDirectory" );

	private GoogleWorkspaceEndpointVerificationConnectorConfig() {
	}
}

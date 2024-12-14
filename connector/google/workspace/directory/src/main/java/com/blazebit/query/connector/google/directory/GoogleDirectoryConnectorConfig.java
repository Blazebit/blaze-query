/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.directory;

import com.blazebit.query.spi.DataFetcherConfig;
import com.google.api.services.directory.Directory;

/**
 * The configuration properties for the Google Workspace directory connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GoogleDirectoryConnectorConfig {

	/**
	 * Specifies the {@link Directory} to use for querying data.
	 */
	public static final DataFetcherConfig<Directory> GOOGLE_DIRECTORY_SERVICE = DataFetcherConfig.forPropertyName( "googleDirectory" );

	private GoogleDirectoryConnectorConfig() {
	}
}

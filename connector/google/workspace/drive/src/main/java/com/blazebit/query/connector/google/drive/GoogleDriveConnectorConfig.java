/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.drive;

import com.blazebit.query.spi.DataFetcherConfig;
import com.google.api.services.drive.Drive;

/**
 * The configuration properties for the Google Workspace drive connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GoogleDriveConnectorConfig {

	/**
	 * Specifies the {@link Drive} to use for querying data.
	 */
	public static final DataFetcherConfig<Drive> GOOGLE_DRIVE_SERVICE = DataFetcherConfig.forPropertyName( "googleDrive" );

	private GoogleDriveConnectorConfig() {
	}
}

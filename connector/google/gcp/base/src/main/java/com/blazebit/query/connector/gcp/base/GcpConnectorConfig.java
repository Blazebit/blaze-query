/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.base;

import com.blazebit.query.spi.DataFetcherConfig;
import com.google.api.gax.core.CredentialsProvider;

/**
 * The configuration properties for the GCP connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GcpConnectorConfig {

	/**
	 * Specifies the {@link CredentialsProvider} to use for querying data.
	 */
	public static final DataFetcherConfig<CredentialsProvider> GCP_CREDENTIALS_PROVIDER = DataFetcherConfig.forPropertyName( "gcpCredentialsProvider" );

	private GcpConnectorConfig() {
	}
}

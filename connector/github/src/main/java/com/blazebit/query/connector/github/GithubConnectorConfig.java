/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github;

import com.blazebit.query.spi.DataFetcherConfig;
import org.kohsuke.github.GitHub;

/**
 * The configuration properties for the Github connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GithubConnectorConfig {

	/**
	 * Specifies the {@link GitHub} to use for querying data.
	 */
	public static final DataFetcherConfig<GitHub> GITHUB = DataFetcherConfig.forPropertyName( "github" );

	private GithubConnectorConfig() {
	}
}

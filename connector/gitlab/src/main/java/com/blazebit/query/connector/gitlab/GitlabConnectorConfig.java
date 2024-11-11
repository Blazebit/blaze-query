/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gitlab;

import com.blazebit.query.spi.DataFetcherConfig;
import org.gitlab4j.api.GitLabApi;

/**
 * The configuration properties for the Gitlab connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class GitlabConnectorConfig {

	/**
	 * Specifies the {@link GitLabApi} to use for querying data.
	 */
	public static final DataFetcherConfig<GitLabApi> GITLAB_API = DataFetcherConfig.forPropertyName( "gitlabApi" );

	private GitlabConnectorConfig() {
	}
}

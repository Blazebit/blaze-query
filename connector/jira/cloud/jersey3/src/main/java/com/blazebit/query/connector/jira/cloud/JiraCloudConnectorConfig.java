/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import com.blazebit.query.connector.jira.cloud.invoker.ApiClient;
import com.blazebit.query.spi.DataFetcherConfig;

/**
 * The configuration properties for the Jira Cloud connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class JiraCloudConnectorConfig {

	/**
	 * Specified the {@link ApiClient} to use for querying data.
	 */
	public static final DataFetcherConfig<ApiClient> API_CLIENT = DataFetcherConfig.forPropertyName(
			"jiraCloudApiClient" );

	public static final DataFetcherConfig<String> JQL_QUERY = DataFetcherConfig.forPropertyName(
			"jqlQuery" );

	private JiraCloudConnectorConfig() {
	}
}

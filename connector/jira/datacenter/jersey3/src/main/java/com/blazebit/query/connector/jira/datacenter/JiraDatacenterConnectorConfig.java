/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.datacenter;

import com.blazebit.query.connector.jira.datacenter.invoker.ApiClient;
import com.blazebit.query.spi.DataFetcherConfig;

/**
 * The configuration properties for the Jira connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class JiraDatacenterConnectorConfig {

	/**
	 * Specified the {@link ApiClient} to use for querying data.
	 */
	public static final DataFetcherConfig<ApiClient> API_CLIENT = DataFetcherConfig.forPropertyName(
			"jiraDatacenterApiClient" );

	private JiraDatacenterConnectorConfig() {
	}
}

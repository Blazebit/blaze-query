/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud.admin;

import com.blazebit.query.connector.jira.cloud.admin.invoker.ApiClient;
import com.blazebit.query.spi.DataFetcherConfig;

/**
 * The configuration properties for the Jira Cloud User Management connector.
 *
 * @author Dimitar Prisadnikov
 * @since 1.0.x
 */
public final class JiraCloudAdminConnectorConfig {

	/**
	 * Specified the {@link ApiClient} to use for querying data.
	 */
	public static final DataFetcherConfig<ApiClient> API_CLIENT = DataFetcherConfig.forPropertyName(
			"jiraCloudUserManagementApiClient" );

	private JiraCloudAdminConnectorConfig() {
	}
}

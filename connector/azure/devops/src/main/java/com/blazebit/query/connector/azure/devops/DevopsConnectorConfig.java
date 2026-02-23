/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.devops;

import com.blazebit.query.connector.devops.invoker.ApiClient;
import com.blazebit.query.spi.DataFetcherConfig;

/**
 * The configuration properties for the Azure DevOps connector.
 *
 * @author Dimitar Prisadnikov
 * @since 1.0.8
 */
public final class DevopsConnectorConfig {

	/**
	 * Specified the {@link ApiClient} to use for querying data.
	 */
	public static final DataFetcherConfig<ApiClient> API_CLIENT = DataFetcherConfig.forPropertyName(
			"azureDevopsApiClient" );

	/**
	 * Specifies the {@link ApiClient} to use for querying Work Item Tracking (WIT) data.
	 * The base URL should be set to {@code https://dev.azure.com}.
	 */
	public static final DataFetcherConfig<ApiClient> WIT_API_CLIENT = DataFetcherConfig.forPropertyName(
			"azureDevopsWitApiClient" );

	/**
	 * Specifies the Azure DevOps organization name.
	 */
	public static final DataFetcherConfig<String> ORGANIZATION = DataFetcherConfig.forPropertyName(
			"azureDevopsOrganization" );

	/**
	 * Specifies the Azure DevOps project name or ID.
	 */
	public static final DataFetcherConfig<String> PROJECT = DataFetcherConfig.forPropertyName(
			"azureDevopsProject" );

	/**
	 * Specifies the WIQL query used to select work items. Defaults to
	 * {@code SELECT [System.Id] FROM WorkItems} when not set.
	 */
	public static final DataFetcherConfig<String> WIQL_QUERY = DataFetcherConfig.forPropertyName(
			"azureDevopsWiqlQuery" );

	private DevopsConnectorConfig() {
	}
}

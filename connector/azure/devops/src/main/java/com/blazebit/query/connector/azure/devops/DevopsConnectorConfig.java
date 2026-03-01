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
	 * Specifies the {@link Account} to use for querying data.
	 */
	public static final DataFetcherConfig<Account> ACCOUNT = DataFetcherConfig.forPropertyName(
			"azureDevopsAccount" );

	/**
	 * Specifies the WIQL query used to select work items. Defaults to
	 * {@code SELECT [System.Id] FROM WorkItems} when not set.
	 */
	public static final DataFetcherConfig<String> WIQL_QUERY = DataFetcherConfig.forPropertyName(
			"azureDevopsWiqlQuery" );

	private DevopsConnectorConfig() {
	}

	/**
	 * Bundles the two API clients together with the organization and project required by the
	 * Azure DevOps connector. {@code apiClient} should point to
	 * {@code https://app.vssps.visualstudio.com} (accounts / profile / Git APIs) and
	 * {@code witApiClient} should point to {@code https://dev.azure.com} (Work Item Tracking API).
	 *
	 * @author Martijn Sprengers
	 * @since 1.0.8
	 */
	public static final class Account {

		private final ApiClient apiClient;
		private final ApiClient witApiClient;
		private final String organization;
		private final String project;

		/**
		 * Creates a new Azure DevOps account configuration.
		 *
		 * @param apiClient the API client for the accounts/profile/Git endpoints
		 *   ({@code https://app.vssps.visualstudio.com})
		 * @param witApiClient the API client for the Work Item Tracking endpoints
		 *   ({@code https://dev.azure.com})
		 * @param organization the Azure DevOps organization name
		 * @param project the Azure DevOps project name or ID
		 */
		public Account(ApiClient apiClient, ApiClient witApiClient, String organization, String project) {
			this.apiClient = apiClient;
			this.witApiClient = witApiClient;
			this.organization = organization;
			this.project = project;
		}

		/**
		 * Returns the API client for the accounts/profile/Git endpoints.
		 *
		 * @return the API client
		 */
		public ApiClient getApiClient() {
			return apiClient;
		}

		/**
		 * Returns the API client for the Work Item Tracking endpoints.
		 *
		 * @return the WIT API client
		 */
		public ApiClient getWitApiClient() {
			return witApiClient;
		}

		/**
		 * Returns the Azure DevOps organization name.
		 *
		 * @return the organization name
		 */
		public String getOrganization() {
			return organization;
		}

		/**
		 * Returns the Azure DevOps project name or ID.
		 *
		 * @return the project name or ID
		 */
		public String getProject() {
			return project;
		}
	}
}

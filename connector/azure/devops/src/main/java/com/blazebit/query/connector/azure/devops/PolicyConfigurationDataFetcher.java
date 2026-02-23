/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.devops;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.devops.api.PolicyConfigurationsApi;
import com.blazebit.query.connector.devops.api.RepositoriesApi;
import com.blazebit.query.connector.devops.invoker.ApiClient;
import com.blazebit.query.connector.devops.invoker.ApiException;
import com.blazebit.query.connector.devops.invoker.ApiResponse;
import com.blazebit.query.connector.devops.model.GitRepository;
import com.blazebit.query.connector.devops.model.PolicyConfiguration;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fetches all {@link PolicyConfiguration} objects across all repositories in a configured
 * Azure DevOps project. Iterates over every repository and pages through its policy
 * configurations using the {@code x-ms-continuationtoken} response header.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class PolicyConfigurationDataFetcher implements DataFetcher<PolicyConfiguration>, Serializable {

	public static final PolicyConfigurationDataFetcher INSTANCE = new PolicyConfigurationDataFetcher();

	private static final String CONTINUATION_TOKEN_HEADER = "x-ms-continuationtoken";

	private PolicyConfigurationDataFetcher() {
	}

	@Override
	public List<PolicyConfiguration> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = DevopsConnectorConfig.API_CLIENT.getAll( context );
			List<String> organizations = DevopsConnectorConfig.ORGANIZATION.getAll( context );
			List<String> projects = DevopsConnectorConfig.PROJECT.getAll( context );
			// Use a map to deduplicate: project-wide policies appear for every repo they match
			Map<Integer, PolicyConfiguration> deduplicated = new LinkedHashMap<>();
			for ( int i = 0; i < apiClients.size(); i++ ) {
				ApiClient apiClient = apiClients.get( i );
				String organization = organizations.get( i );
				String project = projects.get( i );

				fetchForProject( apiClient, organization, project, deduplicated );

				RepositoriesApi repositoriesApi = new RepositoriesApi( apiClient );
				List<GitRepository> repositories = repositoriesApi.repositoriesList(
						organization, project, "7.1", null, null, null );
				for ( GitRepository repository : repositories ) {
					fetchForRepository( apiClient, organization, project, repository, deduplicated );
				}
			}
			return new ArrayList<>( deduplicated.values() );
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch policy configuration list", e );
		}
	}

	private void fetchForProject(ApiClient apiClient, String organization, String project,
			Map<Integer, PolicyConfiguration> target) throws ApiException {
		PolicyConfigurationsApi api = new PolicyConfigurationsApi( apiClient );
		String continuationToken = null;
		do {
			ApiResponse<List<PolicyConfiguration>> response = api.policyConfigurationsGetWithHttpInfo(
					organization, project, "7.1", null, null, null, null, continuationToken );
			for ( PolicyConfiguration policy : response.getData() ) {
				target.putIfAbsent( policy.getId(), policy );
			}
			continuationToken = extractContinuationToken( response.getHeaders() );
		}
		while ( continuationToken != null );
	}

	private void fetchForRepository(ApiClient apiClient, String organization, String project,
			GitRepository repository, Map<Integer, PolicyConfiguration> target) throws ApiException {
		PolicyConfigurationsApi api = new PolicyConfigurationsApi( apiClient );
		String continuationToken = null;
		do {
			ApiResponse<List<PolicyConfiguration>> response = api.policyConfigurationsGetWithHttpInfo(
					organization, project, "7.1", repository.getId(), null, null, null, continuationToken );
			for ( PolicyConfiguration policy : response.getData() ) {
				target.putIfAbsent( policy.getId(), policy );
			}
			continuationToken = extractContinuationToken( response.getHeaders() );
		}
		while ( continuationToken != null );
	}

	private String extractContinuationToken(Map<String, List<String>> headers) {
		for ( Map.Entry<String, List<String>> entry : headers.entrySet() ) {
			if ( CONTINUATION_TOKEN_HEADER.equalsIgnoreCase( entry.getKey() ) ) {
				List<String> values = entry.getValue();
				if ( values != null && !values.isEmpty() ) {
					return values.get( 0 );
				}
			}
		}
		return null;
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( PolicyConfiguration.class, DevopsConventionContext.INSTANCE );
	}
}

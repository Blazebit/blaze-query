/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.devops;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.devops.api.RepositoriesApi;
import com.blazebit.query.connector.devops.invoker.ApiClient;
import com.blazebit.query.connector.devops.invoker.ApiException;
import com.blazebit.query.connector.devops.model.GitRepository;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link GitRepository} objects for a configured Azure DevOps project.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class RepositoryDataFetcher implements DataFetcher<GitRepository>, Serializable {

	public static final RepositoryDataFetcher INSTANCE = new RepositoryDataFetcher();

	private RepositoryDataFetcher() {
	}

	@Override
	public List<GitRepository> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = DevopsConnectorConfig.API_CLIENT.getAll( context );
			List<String> organizations = DevopsConnectorConfig.ORGANIZATION.getAll( context );
			List<String> projects = DevopsConnectorConfig.PROJECT.getAll( context );
			List<GitRepository> list = new ArrayList<>();
			for ( int i = 0; i < apiClients.size(); i++ ) {
				RepositoriesApi repositoriesApi = new RepositoriesApi( apiClients.get( i ) );
				List<GitRepository> repositories = repositoriesApi.repositoriesList(
						organizations.get( i ), projects.get( i ), "7.1", null, null, null );
				list.addAll( repositories );
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch repository list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GitRepository.class, DevopsConventionContext.INSTANCE );
	}
}

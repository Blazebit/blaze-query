/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import org.kohsuke.github.GitHub;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class RepositoryDataFetcher implements DataFetcher<GHRepoWrapper>, Serializable {

	public static final RepositoryDataFetcher INSTANCE = new RepositoryDataFetcher();

	private RepositoryDataFetcher() {
	}

	@Override
	public List<GHRepoWrapper> fetch(DataFetchContext context) {
		try {
			List<GitHub> gitHubs = GithubConnectorConfig.GITHUB.getAll( context );
			List<GHRepoWrapper> list = new ArrayList<>();
			for ( GitHub gitHub : gitHubs ) {
				for( var organization : gitHub.getMyOrganizations().values() ) {
					var repositories = organization.listRepositories().toList();
					repositories.forEach(repository -> {
						list.add( new GHRepoWrapper( repository ) );
					});
				}
			}
			return list;
		}
		catch (IOException | RuntimeException e) {
			throw new DataFetcherException( "Could not fetch repository list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GHRepoWrapper.class, GithubConventionContext.INSTANCE );
	}
}

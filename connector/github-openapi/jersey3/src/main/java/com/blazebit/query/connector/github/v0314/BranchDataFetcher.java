/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.github.v0314;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.github.v0314.api.ReposApi;
import com.blazebit.query.connector.github.v0314.invoker.ApiClient;
import com.blazebit.query.connector.github.v0314.invoker.ApiException;
import com.blazebit.query.connector.github.v0314.model.Repository;
import com.blazebit.query.connector.github.v0314.model.ShortBranch;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class BranchDataFetcher implements DataFetcher<ShortBranch>, Serializable {

	public static final BranchDataFetcher INSTANCE = new BranchDataFetcher();

	private BranchDataFetcher() {
	}

	@Override
	public List<ShortBranch> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = GithubConnectorConfig.API_CLIENT.getAll( context );
			List<ShortBranch> list = new ArrayList<>();
			List<? extends Repository> repositories = context.getSession().getOrFetch( Repository.class );
			for ( ApiClient apiClient : apiClients ) {
				ReposApi reposApi = new ReposApi( apiClient );
				for ( Repository repository : repositories ) {
					for ( int page = 1; ; page++ ) {
						List<ShortBranch> branches = reposApi.reposListBranches(
								repository.getOwner().getLogin(),
								repository.getName(),
								null,
								100,
								page
						);
						list.addAll( branches );
						if ( branches.size() != 100 ) {
							break;
						}
					}
				}
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch branch list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( ShortBranch.class, GithubConventionContext.INSTANCE );
	}
}

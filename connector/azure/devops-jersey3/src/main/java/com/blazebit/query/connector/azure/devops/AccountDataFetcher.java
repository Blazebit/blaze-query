/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.devops;

import com.blazebit.query.connector.azure.devops.api.AccountsApi;
import com.blazebit.query.connector.azure.devops.invoker.ApiClient;
import com.blazebit.query.connector.azure.devops.invoker.ApiException;
import com.blazebit.query.connector.azure.devops.model.Account;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.8
 */
public class AccountDataFetcher implements DataFetcher<Account>, Serializable {

	public static final AccountDataFetcher INSTANCE = new AccountDataFetcher();

	private AccountDataFetcher() {
	}

	@Override
	public List<Account> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = DevopsConnectorConfig.API_CLIENT.getAll( context );
			List<Account> list = new ArrayList<>();
			for ( ApiClient apiClient : apiClients ) {
				AccountsApi accountsApi = new AccountsApi( apiClient );

				//TODO: Pagination?
				List<Account> organizations = accountsApi.accountsList(
						"7.1", UUID.randomUUID(), UUID.randomUUID(), "todo"
				);
				list.addAll( organizations );
				if ( organizations.size() != 100 ) {
					break;
				}
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch account list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( Account.class, DevopsConventionContext.INSTANCE );
	}
}

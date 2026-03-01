/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.devops;


import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.devops.api.AccountsApi;
import com.blazebit.query.connector.devops.api.ProfilesApi;
import com.blazebit.query.connector.devops.invoker.ApiException;
import com.blazebit.query.connector.devops.model.Account;
import com.blazebit.query.connector.devops.model.Profile;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
			List<DevopsConnectorConfig.Account> accounts = DevopsConnectorConfig.ACCOUNT.getAll( context );
			List<Account> list = new ArrayList<>();
			for ( DevopsConnectorConfig.Account account : accounts ) {
				ProfilesApi profilesApi = new ProfilesApi( account.getApiClient() );
				Profile profile = profilesApi.profilesGet( "me", "7.1", null, null, null, null, null );

				AccountsApi accountsApi = new AccountsApi( account.getApiClient() );
				List<Account> fetched = accountsApi.accountsList( "7.1", null, profile.getId(), null );
				list.addAll( fetched );
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

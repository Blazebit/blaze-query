/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.storage.accounts.v20230501;

import java.util.Map;

import com.blazebit.query.connector.azure.storage.accounts.v20230501.model.StorageAccount;
import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

/**
 * The schema provider for the Azure Storage Accounts connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AzureStorageAccountsSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public AzureStorageAccountsSchemaProvider() {
	}

	@Override
	public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Map.<Class<?>, DataFetcher<?>>of(
				StorageAccount.class, StorageAccountsDataFetcher.INSTANCE
		);
	}
}

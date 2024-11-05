/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.storage.fluent.models.StorageAccountInner;
import com.azure.resourcemanager.storage.models.StorageAccount;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class StorageAccountDataFetcher implements DataFetcher<StorageAccountInner>, Serializable {

	public static final StorageAccountDataFetcher INSTANCE = new StorageAccountDataFetcher();

	private StorageAccountDataFetcher() {
	}

	@Override
	public List<StorageAccountInner> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll(
					context );
			List<StorageAccountInner> list = new ArrayList<>();
			for ( AzureResourceManager resourceManager : resourceManagers ) {
				for ( StorageAccount storageAccount : resourceManager.storageAccounts().list() ) {
					list.add( storageAccount.innerModel() );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch storage account list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( StorageAccountInner.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}
}

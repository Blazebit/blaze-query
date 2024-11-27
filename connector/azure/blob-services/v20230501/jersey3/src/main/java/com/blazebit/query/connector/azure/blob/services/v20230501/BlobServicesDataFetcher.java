/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.blob.services.v20230501;

import com.blazebit.query.spi.DataFetcherException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.azure.base.AzureConnectorConfig;
import com.blazebit.query.connector.azure.base.invoker.ApiClient;
import com.blazebit.query.connector.azure.base.invoker.ApiException;
import com.blazebit.query.connector.azure.blob.services.v20230501.api.BlobServiceApi;
import com.blazebit.query.connector.azure.blob.services.v20230501.model.BlobServiceItems;
import com.blazebit.query.connector.azure.blob.services.v20230501.model.BlobServiceProperties;
import com.blazebit.query.connector.azure.storage.accounts.v20230501.model.StorageAccount;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class BlobServicesDataFetcher implements DataFetcher<BlobServiceProperties>, Serializable {

	public static final BlobServicesDataFetcher INSTANCE = new BlobServicesDataFetcher();

	private BlobServicesDataFetcher() {
	}

	@Override
	public List<BlobServiceProperties> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = AzureConnectorConfig.API_CLIENT.getAll( context );
			List<BlobServiceProperties> list = new ArrayList<>();
			for ( ApiClient apiClient : apiClients ) {
				BlobServiceApi blobServiceApi = new BlobServiceApi( apiClient );
				for ( StorageAccount storageAccount : context.getSession().getOrFetch( StorageAccount.class ) ) {
					// Format: /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/{resourceProviderNamespace}/{resourceType}/{resourceName}
					String[] splitParts = storageAccount.getId().split( "/" );
					assert splitParts.length == 9;
					String subscriptionId = splitParts[2];
					String resourceGroupName = splitParts[4];
					String storageAccountName = splitParts[8];
					BlobServiceItems blobServiceItems = blobServiceApi.blobServicesList(
							resourceGroupName,
							storageAccountName,
							"2023-05-01",
							subscriptionId
					);
					list.addAll( blobServiceItems.getValue() );
				}
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch blob service properties list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( BlobServiceProperties.class );
	}
}

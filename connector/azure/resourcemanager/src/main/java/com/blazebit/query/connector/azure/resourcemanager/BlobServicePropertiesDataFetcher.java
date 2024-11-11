/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.storage.fluent.models.BlobServicePropertiesInner;
import com.azure.resourcemanager.storage.fluent.models.StorageAccountInner;
import com.azure.resourcemanager.storage.models.BlobServiceProperties;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class BlobServicePropertiesDataFetcher implements DataFetcher<BlobServicePropertiesInner>, Serializable {

	public static final BlobServicePropertiesDataFetcher INSTANCE = new BlobServicePropertiesDataFetcher();

	private BlobServicePropertiesDataFetcher() {
	}

	@Override
	public List<BlobServicePropertiesInner> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll(
					context );
			List<BlobServicePropertiesInner> list = new ArrayList<>();
			for ( AzureResourceManager resourceManager : resourceManagers ) {
				for ( StorageAccountInner storageAccount : context.getSession()
						.getOrFetch( StorageAccountInner.class ) ) {
					// Format: /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/{resourceProviderNamespace}/{resourceType}/{resourceName}
					String[] splitParts = storageAccount.id().split( "/" );
					assert splitParts.length == 9;
					String subscriptionId = splitParts[2];
					String resourceGroupName = splitParts[4];
					String storageAccountName = splitParts[8];
					if ( resourceManager.subscriptionId().equals( subscriptionId ) ) {
						BlobServiceProperties blobServiceProperties = resourceManager.storageBlobServices()
								.getServicePropertiesAsync(
										resourceGroupName,
										storageAccountName
								).block();
						if ( blobServiceProperties != null ) {
							list.add( blobServiceProperties.innerModel() );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch blob service properties list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( BlobServicePropertiesInner.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}
}

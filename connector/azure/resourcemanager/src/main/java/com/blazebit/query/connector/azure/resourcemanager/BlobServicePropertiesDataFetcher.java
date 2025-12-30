/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.storage.models.BlobServiceProperties;
import com.azure.resourcemanager.storage.models.Kind;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class BlobServicePropertiesDataFetcher implements DataFetcher<AzureResourceBlobServiceProperties>, Serializable {

	public static final BlobServicePropertiesDataFetcher INSTANCE = new BlobServicePropertiesDataFetcher();

	private BlobServicePropertiesDataFetcher() {
	}

	@Override
	public List<AzureResourceBlobServiceProperties> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll(
					context );
			List<AzureResourceBlobServiceProperties> list = new ArrayList<>();
			for ( AzureResourceManager resourceManager : resourceManagers ) {
				for ( AzureResourceStorageAccount storageAccount : context.getSession()
						.getOrFetch( AzureResourceStorageAccount.class ) ) {
					if ( resourceManager.subscriptionId().equals( storageAccount.getSubscriptionId() )
							&& storageAccount.getPayload().kind() != Kind.FILE_STORAGE ) {
						BlobServiceProperties blobServiceProperties = resourceManager.storageBlobServices()
								.getServicePropertiesAsync(
										storageAccount.getResourceGroupName(),
										storageAccount.getResourceName()
								).block();
						if ( blobServiceProperties != null ) {
							list.add( new AzureResourceBlobServiceProperties(
									resourceManager.tenantId(),
									blobServiceProperties.id(),
									blobServiceProperties.innerModel()
							) );
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
		return DataFormats.componentMethodConvention( AzureResourceBlobServiceProperties.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}
}

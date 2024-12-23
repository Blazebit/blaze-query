/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ResourceGroupDataFetcher implements DataFetcher<AzureResourceManagerResourceGroup>, Serializable {

	public static final ResourceGroupDataFetcher INSTANCE = new ResourceGroupDataFetcher();

	private ResourceGroupDataFetcher() {
	}

	@Override
	public List<AzureResourceManagerResourceGroup> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll(
					context );
			List<AzureResourceManagerResourceGroup> list = new ArrayList<>();
			for ( AzureResourceManager resourceManager : resourceManagers ) {
				for ( ResourceGroup resourceGroup : resourceManager.resourceGroups().list() ) {
					// Format: /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}
					String[] splitParts = resourceGroup.id().split( "/" );
					assert splitParts.length == 5;
					String subscriptionId = splitParts[2];
					assert resourceGroup.name().equals( splitParts[4] );
					list.add( new AzureResourceManagerResourceGroup(
							resourceManager.tenantId(),
							subscriptionId,
							resourceGroup.name(),
							resourceGroup.innerModel()
					) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch resource group list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AzureResourceManagerResourceGroup.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.resources.fluent.models.ResourceGroupInner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.keyvault.fluent.models.VaultInner;
import com.azure.resourcemanager.keyvault.models.Vault;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class VaultDataFetcher implements DataFetcher<VaultInner>, Serializable {

	public static final VaultDataFetcher INSTANCE = new VaultDataFetcher();

	private VaultDataFetcher() {
	}

	@Override
	public List<VaultInner> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll(
					context );
			List<VaultInner> list = new ArrayList<>();
			for ( AzureResourceManager resourceManager : resourceManagers ) {
				for ( ResourceGroupInner resourceGroup : context.getSession().getOrFetch(
						ResourceGroupInner.class ) ) {
					for ( Vault vault : resourceManager.vaults().listByResourceGroup( resourceGroup.name() ) ) {
						list.add( vault.innerModel() );
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch vault list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( VaultInner.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}
}

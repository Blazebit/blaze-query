/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.azure.resourcemanager.AzureResourceManager;
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
public class VaultDataFetcher implements DataFetcher<AzureResourceVault>, Serializable {

	public static final VaultDataFetcher INSTANCE = new VaultDataFetcher();

	private VaultDataFetcher() {
	}

	@Override
	public List<AzureResourceVault> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll(
					context );
			List<AzureResourceVault> list = new ArrayList<>();
			List<? extends AzureResourceManagerResourceGroup> resourceGroups = context.getSession().getOrFetch(
					AzureResourceManagerResourceGroup.class );
			for ( AzureResourceManager resourceManager : resourceManagers ) {
				for ( AzureResourceManagerResourceGroup resourceGroup : resourceGroups ) {
					if ( resourceManager.subscriptionId().equals( resourceGroup.getSubscriptionId() ) ) {
						for ( Vault vault : resourceManager.vaults()
								.listByResourceGroup( resourceGroup.getResourceGroupName() ) ) {
							list.add( new AzureResourceVault(
									resourceManager.tenantId(),
									vault.id(),
									vault.innerModel()
							) );
						}
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
		return DataFormats.componentMethodConvention( AzureResourceVault.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}
}

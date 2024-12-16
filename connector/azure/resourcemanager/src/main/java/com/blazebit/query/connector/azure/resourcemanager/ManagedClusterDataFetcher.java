/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.containerservice.models.KubernetesCluster;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martijn Sprengers
 * @since 1.0.0
 */
public class ManagedClusterDataFetcher implements DataFetcher<AzureResourceManagerManagedCluster>, Serializable {

	public static final ManagedClusterDataFetcher INSTANCE = new ManagedClusterDataFetcher();

	private ManagedClusterDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AzureResourceManagerManagedCluster.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}

	@Override
	public List<AzureResourceManagerManagedCluster> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll(
					context );
			List<AzureResourceManagerManagedCluster> list = new ArrayList<>();
			for ( AzureResourceManager resourceManager : resourceManagers ) {
				for ( KubernetesCluster kubernetesCluster : resourceManager.kubernetesClusters().list() ) {
					list.add( new AzureResourceManagerManagedCluster(
							resourceManager.tenantId(),
							kubernetesCluster.id(),
							kubernetesCluster.innerModel()
					) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch managed cluster list", e );
		}
	}
}

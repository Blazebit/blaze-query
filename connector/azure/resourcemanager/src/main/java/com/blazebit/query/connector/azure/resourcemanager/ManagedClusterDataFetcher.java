package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.containerservice.fluent.models.ManagedClusterInner;
import com.azure.resourcemanager.containerservice.models.KubernetesCluster;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ManagedClusterDataFetcher implements DataFetcher<ManagedClusterInner>, Serializable {

	public static final ManagedClusterDataFetcher INSTANCE = new ManagedClusterDataFetcher();

	private ManagedClusterDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( ManagedClusterInner.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}

	@Override
	public List<ManagedClusterInner> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll(
					context );
			List<ManagedClusterInner> list = new ArrayList<>();
			for ( AzureResourceManager resourceManager : resourceManagers ) {
				for ( KubernetesCluster kubernetesCluster : resourceManager.kubernetesClusters().list() ) {
					list.add( kubernetesCluster.innerModel() );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch virtual machine list", e );
		}
	}
}

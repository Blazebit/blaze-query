/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.network.fluent.models.VirtualNetworkInner;
import com.azure.resourcemanager.network.models.Network;
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
public class VirtualNetworkDataFetcher implements DataFetcher<VirtualNetworkInner>, Serializable {

	public static final VirtualNetworkDataFetcher INSTANCE = new VirtualNetworkDataFetcher();

	private VirtualNetworkDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( VirtualNetworkInner.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}

	@Override
	public List<VirtualNetworkInner> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll(
					context );
			List<VirtualNetworkInner> list = new ArrayList<>();
			for ( AzureResourceManager resourceManager : resourceManagers ) {
				for ( Network networkManager : resourceManager.networks().list() ) {
					list.add( networkManager.innerModel() );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch virtual network list", e );
		}
	}
}

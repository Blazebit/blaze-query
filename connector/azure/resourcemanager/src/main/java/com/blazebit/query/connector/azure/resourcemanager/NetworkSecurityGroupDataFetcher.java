/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.network.models.NetworkSecurityGroup;
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
 * @since 1.0.2
 */
public class NetworkSecurityGroupDataFetcher implements DataFetcher<AzureResourceNetworkSecurityGroup>, Serializable {

	public static final NetworkSecurityGroupDataFetcher INSTANCE = new NetworkSecurityGroupDataFetcher();

	private NetworkSecurityGroupDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AzureResourceNetworkSecurityGroup.class, AzureResourceManagerConventionContext.INSTANCE);
	}

	@Override
	public List<AzureResourceNetworkSecurityGroup> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll(
					context );
			List<AzureResourceNetworkSecurityGroup> list = new ArrayList<>();
			for ( AzureResourceManager resourceManager : resourceManagers ) {
				for ( NetworkSecurityGroup networkSecurityGroup : resourceManager.networkSecurityGroups().list() ) {
					list.add( new AzureResourceNetworkSecurityGroup(
							resourceManager.tenantId(),
							networkSecurityGroup.id(),
							networkSecurityGroup.innerModel()
					) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch network security groups", e );
		}
	}
}

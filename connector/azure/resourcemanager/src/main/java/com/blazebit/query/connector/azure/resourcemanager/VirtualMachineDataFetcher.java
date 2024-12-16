/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class VirtualMachineDataFetcher implements DataFetcher<AzureResourceManagerVirtualMachine>, Serializable {

	public static final VirtualMachineDataFetcher INSTANCE = new VirtualMachineDataFetcher();

	private VirtualMachineDataFetcher() {
	}

	@Override
	public List<AzureResourceManagerVirtualMachine> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll(
					context );
			List<AzureResourceManagerVirtualMachine> list = new ArrayList<>();
			for ( AzureResourceManager resourceManager : resourceManagers ) {
				for ( VirtualMachine virtualMachine : resourceManager.virtualMachines().list() ) {
					list.add( new AzureResourceManagerVirtualMachine(
							resourceManager.tenantId(),
							virtualMachine.id(),
							virtualMachine.innerModel()
					) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch virtual machine list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AzureResourceManagerVirtualMachine.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}
}

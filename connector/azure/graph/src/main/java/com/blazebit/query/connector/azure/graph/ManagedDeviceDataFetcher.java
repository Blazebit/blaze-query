/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.microsoft.graph.beta.models.ManagedDevice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Hovens
 * @since 1.0.0
 */
public class ManagedDeviceDataFetcher implements DataFetcher<ManagedDevice>, Serializable {

	public static final ManagedDeviceDataFetcher INSTANCE = new ManagedDeviceDataFetcher();

	private ManagedDeviceDataFetcher() {
	}

	/**
	 * @param context The data fetching context
	 * @return A list of ManagedDevices
	 * @throws com.microsoft.graph.beta.models.odataerrors.ODataError with message "AADSTS500014: The service principal for resource '0000000a-0000-0000-c000-000000000000' is disabled. etc....`" when Microsoft Intune is not activated on the tenant
	 */
	@Override
	public List<ManagedDevice> fetch(DataFetchContext context) {
		try {
			List<AzureGraphClientAccessor> accessors = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll( context );
			List<ManagedDevice> list = new ArrayList<>();
			for ( AzureGraphClientAccessor accessor : accessors ) {
				list.addAll( accessor.getGraphServiceClient().deviceManagement().managedDevices().get().getValue() );
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch managed device list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( ManagedDevice.class, AzureGraphConventionContext.INSTANCE );
	}
}

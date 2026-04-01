/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.sentinel;

import com.azure.resourcemanager.securityinsights.models.DataConnector;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A data fetcher for fetching Microsoft Sentinel data connectors.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class SentinelDataConnectorDataFetcher implements DataFetcher<SentinelDataConnector>, Serializable {

	public static final SentinelDataConnectorDataFetcher INSTANCE = new SentinelDataConnectorDataFetcher();

	private SentinelDataConnectorDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( SentinelDataConnector.class, SentinelConventionContext.INSTANCE );
	}

	@Override
	public List<SentinelDataConnector> fetch(DataFetchContext context) {
		try {
			List<SentinelClientAccessor> accessors = SentinelConnectorConfig.SENTINEL_CLIENT.getAll( context );
			List<SentinelDataConnector> list = new ArrayList<>();
			for ( SentinelClientAccessor accessor : accessors ) {
				for ( DataConnector dataConnector : accessor.getManager().dataConnectors()
						.list( accessor.getResourceGroupName(), accessor.getWorkspaceName() ) ) {
					list.add( new SentinelDataConnector(
							accessor.getTenantId(),
							accessor.getSubscriptionId(),
							accessor.getResourceGroupName(),
							accessor.getWorkspaceName(),
							dataConnector.innerModel() ) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch Sentinel data connectors", e );
		}
	}
}

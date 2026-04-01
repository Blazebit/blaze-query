/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.sentinel;

import com.azure.resourcemanager.securityinsights.models.Incident;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A data fetcher for fetching Microsoft Sentinel incidents.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class SentinelIncidentDataFetcher implements DataFetcher<SentinelIncident>, Serializable {

	public static final SentinelIncidentDataFetcher INSTANCE = new SentinelIncidentDataFetcher();

	private SentinelIncidentDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( SentinelIncident.class, SentinelConventionContext.INSTANCE );
	}

	@Override
	public List<SentinelIncident> fetch(DataFetchContext context) {
		try {
			List<SentinelClientAccessor> accessors = SentinelConnectorConfig.SENTINEL_CLIENT.getAll( context );
			List<SentinelIncident> list = new ArrayList<>();
			for ( SentinelClientAccessor accessor : accessors ) {
				for ( Incident incident : accessor.getManager().incidents()
						.list( accessor.getResourceGroupName(), accessor.getWorkspaceName() ) ) {
					list.add( new SentinelIncident(
							accessor.getTenantId(),
							accessor.getSubscriptionId(),
							accessor.getResourceGroupName(),
							accessor.getWorkspaceName(),
							incident.innerModel() ) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch Sentinel incidents", e );
		}
	}
}

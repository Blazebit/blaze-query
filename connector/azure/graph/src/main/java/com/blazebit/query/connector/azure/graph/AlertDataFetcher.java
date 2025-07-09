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
import com.microsoft.graph.beta.models.security.Alert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A data fetcher for fetching Azure Graph alerts.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class AlertDataFetcher implements DataFetcher<AzureGraphAlert>, Serializable {

	public static final AlertDataFetcher INSTANCE = new AlertDataFetcher();

	private AlertDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( AzureGraphAlert.class, AzureGraphConventionContext.INSTANCE );
	}

	@Override
	public List<AzureGraphAlert> fetch(DataFetchContext context) {
		try {
			List<AzureGraphClientAccessor> accessors = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll( context );
			List<AzureGraphAlert> list = new ArrayList<>();
			for ( AzureGraphClientAccessor accessor : accessors ) {
				var alerts = accessor.getGraphServiceClient().security().alertsV2().get().getValue();
				for (Alert alert : alerts) {
					list.add( new AzureGraphAlert( accessor.getTenantId(), alert ) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch alerts", e );
		}
	}
}

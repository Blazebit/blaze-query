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
import com.microsoft.graph.beta.models.security.Incident;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A data fetcher for fetching Azure Graph security incidents.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class IncidentDataFetcher implements DataFetcher<AzureGraphIncident>, Serializable {

	public static final IncidentDataFetcher INSTANCE = new IncidentDataFetcher();

	private IncidentDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( AzureGraphIncident.class, AzureGraphConventionContext.INSTANCE );
	}

	@Override
	public List<AzureGraphIncident> fetch(DataFetchContext context) {
		try {
			List<AzureGraphClientAccessor> accessors = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll( context );
			List<AzureGraphIncident> list = new ArrayList<>();
			for ( AzureGraphClientAccessor accessor : accessors ) {
				var page = accessor.getGraphServiceClient().security().incidents().get();
				while ( page != null && page.getValue() != null ) {
					for ( Incident incident : page.getValue() ) {
						list.add( new AzureGraphIncident( accessor.getTenantId(), incident ) );
					}
					String nextLink = page.getOdataNextLink();
					if ( nextLink == null ) {
						break;
					}
					page = accessor.getGraphServiceClient().security().incidents().withUrl( nextLink ).get();
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch incidents", e );
		}
	}
}

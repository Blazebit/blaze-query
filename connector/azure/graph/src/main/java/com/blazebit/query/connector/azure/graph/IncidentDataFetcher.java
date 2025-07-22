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

import java.util.ArrayList;
import java.util.List;

public class IncidentDataFetcher implements DataFetcher<AzureGraphIncident> {

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
			List<AzureGraphClientAccessor> accessors = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll(context);
			List<AzureGraphIncident> list = new ArrayList<>();
			for (AzureGraphClientAccessor accessor : accessors) {
				var incidents = accessor.getGraphServiceClient().security().incidents().get().getValue();
				for (Incident incident : incidents) {
					list.add(new AzureGraphIncident(accessor.getTenantId(), incident));
				}
			}
			return list;
		} catch (RuntimeException e) {
			throw new DataFetcherException("Could not fetch incidents", e);
		}
	}
}

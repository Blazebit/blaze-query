/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.microsoft.graph.beta.models.Application;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ApplicationDataFetcher implements DataFetcher<AzureGraphApplication>, Serializable {

	public static final ApplicationDataFetcher INSTANCE = new ApplicationDataFetcher();

	private ApplicationDataFetcher() {
	}

	@Override
	public List<AzureGraphApplication> fetch(DataFetchContext context) {
		try {
			List<AzureGraphClientAccessor> accessors = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll( context );
			List<AzureGraphApplication> list = new ArrayList<>();
			for ( AzureGraphClientAccessor accessor : accessors ) {
				for ( Application application : accessor.getGraphServiceClient().applications().get().getValue() ) {
					list.add( new AzureGraphApplication( accessor.getTenantId(), application ) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch application list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( AzureGraphApplication.class, AzureGraphConventionContext.INSTANCE );
	}
}

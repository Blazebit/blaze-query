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
import com.microsoft.graph.beta.models.Organization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martijn Sprengers
 * @since 1.0.0
 */
public class OrganizationDataFetcher implements DataFetcher<AzureGraphOrganization>, Serializable {

	public static final OrganizationDataFetcher INSTANCE = new OrganizationDataFetcher();

	private OrganizationDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( AzureGraphOrganization.class, AzureGraphConventionContext.INSTANCE );
	}

	@Override
	public List<AzureGraphOrganization> fetch(DataFetchContext context) {
		try {
			List<AzureGraphClientAccessor> accessors = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll( context );
			List<AzureGraphOrganization> list = new ArrayList<>();
			for ( AzureGraphClientAccessor accessor : accessors ) {
				for ( Organization organization : accessor.getGraphServiceClient().organization().get().getValue() ) {
					list.add( new AzureGraphOrganization( accessor.getTenantId(), organization ) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch organization list", e );
		}
	}
}

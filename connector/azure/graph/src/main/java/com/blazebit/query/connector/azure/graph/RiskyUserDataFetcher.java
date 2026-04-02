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
import com.microsoft.graph.beta.models.RiskyUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link DataFetcher} for {@link AzureGraphRiskyUser} objects via the Microsoft Graph
 * Identity Protection API ({@code /identityProtection/riskyUsers}).
 *
 * <p>Requires {@link AzureGraphConnectorConfig#GRAPH_SERVICE_CLIENT} to be configured.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class RiskyUserDataFetcher implements DataFetcher<AzureGraphRiskyUser>, Serializable {

	public static final RiskyUserDataFetcher INSTANCE = new RiskyUserDataFetcher();

	private RiskyUserDataFetcher() {
	}

	@Override
	public List<AzureGraphRiskyUser> fetch(DataFetchContext context) {
		try {
			List<AzureGraphClientAccessor> accessors = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll( context );
			List<AzureGraphRiskyUser> list = new ArrayList<>();
			for ( AzureGraphClientAccessor accessor : accessors ) {
				var page = accessor.getGraphServiceClient().identityProtection().riskyUsers().get();
				while ( page != null && page.getValue() != null ) {
					for ( RiskyUser riskyUser : page.getValue() ) {
						list.add( new AzureGraphRiskyUser( accessor.getTenantId(), riskyUser ) );
					}
					String nextLink = page.getOdataNextLink();
					if ( nextLink == null ) {
						break;
					}
					page = accessor.getGraphServiceClient().identityProtection().riskyUsers().withUrl( nextLink ).get();
				}
			}
			return list;
		}
		catch (DataFetcherException e) {
			throw e;
		}
		catch (Exception e) {
			throw new DataFetcherException( "Could not fetch risky user list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( AzureGraphRiskyUser.class, AzureGraphConventionContext.INSTANCE );
	}
}

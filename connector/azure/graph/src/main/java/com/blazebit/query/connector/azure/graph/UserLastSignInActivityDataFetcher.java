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
import com.microsoft.graph.beta.models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martijn Sprengers
 * @since 1.0.0
 */
public class UserLastSignInActivityDataFetcher implements DataFetcher<AzureGraphUserLastSignInActivity>, Serializable {

	public static final UserLastSignInActivityDataFetcher INSTANCE = new UserLastSignInActivityDataFetcher();

	private UserLastSignInActivityDataFetcher() {
	}

	@Override
	public List<AzureGraphUserLastSignInActivity> fetch(DataFetchContext context) {
		try {
			List<AzureGraphClientAccessor> accessors = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll(
					context );
			List<AzureGraphUserLastSignInActivity> list = new ArrayList<>();
			for ( AzureGraphClientAccessor accessor : accessors ) {
				// Calling this API requires service plan "AAD_PREMIUM" or "AAD_PREMIUM_P2"
				for ( User user : accessor.getGraphServiceClient().users()
						.get( getRequestConfiguration -> getRequestConfiguration.queryParameters.select = new String[] {"signInActivity"} )
						.getValue() ) {
					list.add( new AzureGraphUserLastSignInActivity( accessor.getTenantId(), new UserLastSignInActivity( user ) ) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch user sign-in activity list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( AzureGraphUserLastSignInActivity.class, AzureGraphConventionContext.INSTANCE );
	}
}

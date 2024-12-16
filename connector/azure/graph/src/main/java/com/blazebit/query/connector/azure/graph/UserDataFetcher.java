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
 * Data fetcher that fetches Azure users from the Microsoft Graph API
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class UserDataFetcher implements DataFetcher<AzureGraphUser>, Serializable {

	public static final UserDataFetcher INSTANCE = new UserDataFetcher();

	private UserDataFetcher() {
	}

	@Override
	public List<AzureGraphUser> fetch(DataFetchContext context) {
		try {
			List<AzureGraphClientAccessor> accessors = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll(
					context );
			List<AzureGraphUser> list = new ArrayList<>();
			for ( AzureGraphClientAccessor accessor : accessors ) {
				for ( User user : accessor.getGraphServiceClient().users().get().getValue() ) {
					list.add( new AzureGraphUser( accessor.getTenantId(), user ) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch user list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( AzureGraphUser.class, AzureGraphConventionContext.INSTANCE );
	}
}

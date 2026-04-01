/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.linear;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link LinearTeam} entries from the Linear GraphQL API.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public class LinearTeamDataFetcher implements DataFetcher<LinearTeam>, Serializable {

	public static final LinearTeamDataFetcher INSTANCE = new LinearTeamDataFetcher();

	private LinearTeamDataFetcher() {
	}

	@Override
	public List<LinearTeam> fetch(DataFetchContext context) {
		try {
			List<LinearGraphQlClient> clients = LinearConnectorConfig.LINEAR_CLIENT.getAll( context );
			List<LinearTeam> result = new ArrayList<>();
			for ( LinearGraphQlClient client : clients ) {
				result.addAll( client.fetchTeams() );
			}
			return result;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch Linear teams", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( LinearTeam.class, LinearConventionContext.INSTANCE );
	}
}

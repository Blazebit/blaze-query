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
 * Fetches {@link LinearCycle} entries from the Linear GraphQL API.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public class LinearCycleDataFetcher implements DataFetcher<LinearCycle>, Serializable {

	public static final LinearCycleDataFetcher INSTANCE = new LinearCycleDataFetcher();

	private LinearCycleDataFetcher() {
	}

	@Override
	public List<LinearCycle> fetch(DataFetchContext context) {
		try {
			List<LinearGraphQlClient> clients = LinearConnectorConfig.LINEAR_CLIENT.getAll( context );
			List<LinearCycle> result = new ArrayList<>();
			for ( LinearGraphQlClient client : clients ) {
				result.addAll( client.fetchCycles() );
			}
			return result;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch Linear cycles", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( LinearCycle.class, LinearConventionContext.INSTANCE );
	}
}

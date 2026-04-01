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
 * Fetches {@link LinearWorkflowState} entries from the Linear GraphQL API.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public class LinearWorkflowStateDataFetcher implements DataFetcher<LinearWorkflowState>, Serializable {

	public static final LinearWorkflowStateDataFetcher INSTANCE = new LinearWorkflowStateDataFetcher();

	private LinearWorkflowStateDataFetcher() {
	}

	@Override
	public List<LinearWorkflowState> fetch(DataFetchContext context) {
		try {
			List<LinearGraphQlClient> clients = LinearConnectorConfig.LINEAR_CLIENT.getAll( context );
			List<LinearWorkflowState> result = new ArrayList<>();
			for ( LinearGraphQlClient client : clients ) {
				result.addAll( client.fetchWorkflowStates() );
			}
			return result;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch Linear workflow states", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( LinearWorkflowState.class, LinearConventionContext.INSTANCE );
	}
}

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
 * Fetches {@link LinearProject} entries from the Linear GraphQL API.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public class LinearProjectDataFetcher implements DataFetcher<LinearProject>, Serializable {

	public static final LinearProjectDataFetcher INSTANCE = new LinearProjectDataFetcher();

	private LinearProjectDataFetcher() {
	}

	@Override
	public List<LinearProject> fetch(DataFetchContext context) {
		try {
			List<LinearGraphQlClient> clients = LinearConnectorConfig.LINEAR_CLIENT.getAll( context );
			List<LinearProject> result = new ArrayList<>();
			for ( LinearGraphQlClient client : clients ) {
				result.addAll( client.fetchProjects() );
			}
			return result;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch Linear projects", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( LinearProject.class, LinearConventionContext.INSTANCE );
	}
}

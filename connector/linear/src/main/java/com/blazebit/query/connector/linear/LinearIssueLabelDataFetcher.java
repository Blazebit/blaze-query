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
 * Fetches {@link LinearIssueLabel} entries from the Linear GraphQL API.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public class LinearIssueLabelDataFetcher implements DataFetcher<LinearIssueLabel>, Serializable {

	public static final LinearIssueLabelDataFetcher INSTANCE = new LinearIssueLabelDataFetcher();

	private LinearIssueLabelDataFetcher() {
	}

	@Override
	public List<LinearIssueLabel> fetch(DataFetchContext context) {
		try {
			List<LinearGraphQlClient> clients = LinearConnectorConfig.LINEAR_CLIENT.getAll( context );
			List<LinearIssueLabel> result = new ArrayList<>();
			for ( LinearGraphQlClient client : clients ) {
				result.addAll( client.fetchIssueLabels() );
			}
			return result;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch Linear issue labels", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( LinearIssueLabel.class, LinearConventionContext.INSTANCE );
	}
}

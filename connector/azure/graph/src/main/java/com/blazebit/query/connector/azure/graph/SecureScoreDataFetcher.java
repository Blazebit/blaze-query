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
import com.microsoft.graph.beta.models.SecureScore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A data fetcher for fetching Azure Graph secure scores.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class SecureScoreDataFetcher implements DataFetcher<AzureGraphSecureScore>, Serializable {

	public static final SecureScoreDataFetcher INSTANCE = new SecureScoreDataFetcher();

	private SecureScoreDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( AzureGraphSecureScore.class, AzureGraphConventionContext.INSTANCE );
	}

	@Override
	public List<AzureGraphSecureScore> fetch(DataFetchContext context) {
		try {
			List<AzureGraphClientAccessor> accessors = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll( context );
			List<AzureGraphSecureScore> list = new ArrayList<>();
			for ( AzureGraphClientAccessor accessor : accessors ) {
				var page = accessor.getGraphServiceClient().security().secureScores().get();
				while ( page != null && page.getValue() != null ) {
					for ( SecureScore score : page.getValue() ) {
						list.add( new AzureGraphSecureScore( accessor.getTenantId(), score ) );
					}
					String nextLink = page.getOdataNextLink();
					if ( nextLink == null ) {
						break;
					}
					page = accessor.getGraphServiceClient().security().secureScores().withUrl( nextLink ).get();
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch secure scores", e );
		}
	}
}

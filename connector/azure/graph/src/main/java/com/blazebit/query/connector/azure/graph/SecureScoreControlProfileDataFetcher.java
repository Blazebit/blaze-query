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
import com.microsoft.graph.beta.models.SecureScoreControlProfile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A data fetcher for fetching Azure Graph secure score control profiles.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class SecureScoreControlProfileDataFetcher implements DataFetcher<AzureGraphSecureScoreControlProfile>, Serializable {

	public static final SecureScoreControlProfileDataFetcher INSTANCE = new SecureScoreControlProfileDataFetcher();

	private SecureScoreControlProfileDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( AzureGraphSecureScoreControlProfile.class, AzureGraphConventionContext.INSTANCE );
	}

	@Override
	public List<AzureGraphSecureScoreControlProfile> fetch(DataFetchContext context) {
		try {
			List<AzureGraphClientAccessor> accessors = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll( context );
			List<AzureGraphSecureScoreControlProfile> list = new ArrayList<>();
			for ( AzureGraphClientAccessor accessor : accessors ) {
				var page = accessor.getGraphServiceClient().security().secureScoreControlProfiles().get();
				while ( page != null && page.getValue() != null ) {
					for ( SecureScoreControlProfile profile : page.getValue() ) {
						list.add( new AzureGraphSecureScoreControlProfile( accessor.getTenantId(), profile ) );
					}
					String nextLink = page.getOdataNextLink();
					if ( nextLink == null ) {
						break;
					}
					page = accessor.getGraphServiceClient().security().secureScoreControlProfiles().withUrl( nextLink ).get();
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch secure score control profiles", e );
		}
	}
}

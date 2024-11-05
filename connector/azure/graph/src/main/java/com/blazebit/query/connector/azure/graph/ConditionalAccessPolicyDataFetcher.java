/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.microsoft.graph.beta.models.ConditionalAccessPolicy;
import com.microsoft.graph.beta.serviceclient.GraphServiceClient;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ConditionalAccessPolicyDataFetcher implements DataFetcher<ConditionalAccessPolicy>, Serializable {

	public static final ConditionalAccessPolicyDataFetcher INSTANCE = new ConditionalAccessPolicyDataFetcher();

	private ConditionalAccessPolicyDataFetcher() {
	}

	@Override
	public List<ConditionalAccessPolicy> fetch(DataFetchContext context) {
		try {
			List<GraphServiceClient> graphServiceClients = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll(
					context );
			List<ConditionalAccessPolicy> list = new ArrayList<>();
			for ( GraphServiceClient graphServiceClient : graphServiceClients ) {
				list.addAll( graphServiceClient.policies().conditionalAccessPolicies().get().getValue() );
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch conditional access policy list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( ConditionalAccessPolicy.class, AzureGraphConventionContext.INSTANCE );
	}
}

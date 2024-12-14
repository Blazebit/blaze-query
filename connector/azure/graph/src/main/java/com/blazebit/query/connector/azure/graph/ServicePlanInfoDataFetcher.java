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
import com.microsoft.graph.beta.models.ServicePlanInfo;
import com.microsoft.graph.beta.models.SubscribedSku;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DataFetcher that retrieves the service plan the currently logged in Azure client is subscribed to
 *
 * @author Martijn Sprengers
 * @since 1.0.0
 */
public class ServicePlanInfoDataFetcher implements DataFetcher<ServicePlanInfo>, Serializable {

	public static final ServicePlanInfoDataFetcher INSTANCE = new ServicePlanInfoDataFetcher();

	private ServicePlanInfoDataFetcher() {
	}

	@Override
	public List<ServicePlanInfo> fetch(DataFetchContext context) {
		try {
			List<AzureGraphClientAccessor> accessors = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll( context );
			List<SubscribedSku> list = new ArrayList<>();
			for ( AzureGraphClientAccessor accessor : accessors ) {
				list.addAll( accessor.getGraphServiceClient().subscribedSkus().get().getValue() );
			}
			return list.stream()
					.flatMap( subscribedSku -> subscribedSku.getServicePlans().stream() )
					.distinct()
					.collect( Collectors.toList() );
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch subscribed sku list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( ServicePlanInfo.class, AzureGraphConventionContext.INSTANCE );
	}
}

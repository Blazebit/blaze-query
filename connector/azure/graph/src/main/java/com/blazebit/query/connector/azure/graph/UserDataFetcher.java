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
import com.microsoft.graph.beta.models.SubscribedSku;
import com.microsoft.graph.beta.models.User;
import com.microsoft.graph.beta.serviceclient.GraphServiceClient;

import static com.blazebit.query.connector.azure.graph.ServicePlanUtils.getAllServicePlanNames;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class UserDataFetcher implements DataFetcher<User>, Serializable {

	public static final UserDataFetcher INSTANCE = new UserDataFetcher();

	private UserDataFetcher() {
	}

    @Override
    public List<User> fetch(DataFetchContext context) {
        try {
            List<GraphServiceClient> graphServiceClients = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll(context);
            List<User> list = new ArrayList<>();
            for (GraphServiceClient graphServiceClient : graphServiceClients) {
                List<SubscribedSku> subscribedSkus = (List<SubscribedSku>) context.getSession().getOrFetch(SubscribedSku.class);
                List<ServicePlanName> servicePlanNames = getAllServicePlanNames(subscribedSkus);
                if (servicePlanNames.stream().anyMatch(ServicePlanName::isAad)) {
                    // If the serviceplan names includes "AAD_PREMIUM" or "AAD_PREMIUM_P2", also fetch the signInActivity
                    list.addAll(graphServiceClient.users().get(getRequestConfiguration -> getRequestConfiguration.queryParameters.select = new String[]{"signInActivity"}).getValue());
                } else {
                    list.addAll(graphServiceClient.users().get().getValue());
                }
            }
            return list;
        } catch (RuntimeException e) {
            throw new DataFetcherException("Could not fetch user list", e);
        }
    }

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( User.class, AzureGraphConventionContext.INSTANCE );
	}
}

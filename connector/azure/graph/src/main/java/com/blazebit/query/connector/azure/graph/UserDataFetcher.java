/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.query.connector.azure.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.microsoft.graph.beta.models.ServicePlanInfo;
import com.microsoft.graph.beta.models.SubscribedSku;
import com.microsoft.graph.beta.models.User;
import com.microsoft.graph.beta.serviceclient.GraphServiceClient;

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
                List<ServicePlanName> servicePlanNames = getAllServicePlanNames(context.getSession().getOrFetch(SubscribedSku.class));
                if (servicePlanNames.contains("AAD_PREMIUM") || servicePlanNames.contains("AAD_PREMIUM_P2")) {
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
        return DataFormats.beansConvention(User.class, AzureGraphConventionContext.INSTANCE);
    }

    /**
     * Extracts all service plan names from a list of SubscribedSku objects.
     *
     * @param subscribedSkus the list of SubscribedSku
     * @return a list of all service plan names across all SubscribedSku
     */
    private List<String> getAllServicePlanNames(List<SubscribedSku> subscribedSkus) {
        List<String> servicePlanNames = new ArrayList<>();

        for (SubscribedSku sku : subscribedSkus) {
            for (ServicePlanInfo servicePlan : Objects.requireNonNull(sku.getServicePlans())) {
                servicePlanNames.add(servicePlan.getServicePlanName());
            }
        }

        return servicePlanNames;
    }
}

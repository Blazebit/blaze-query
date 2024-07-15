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
            List<GraphServiceClient> graphServiceClients = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll(context);
            List<ConditionalAccessPolicy> list = new ArrayList<>();
            for (GraphServiceClient graphServiceClient : graphServiceClients) {
                list.addAll(graphServiceClient.policies().conditionalAccessPolicies().get().getValue());
            }
            return list;
        } catch (RuntimeException e) {
            throw new DataFetcherException("Could not fetch conditional access policy list", e);
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.beansConvention(ConditionalAccessPolicy.class, AzureGraphConventionContext.INSTANCE);
    }
}

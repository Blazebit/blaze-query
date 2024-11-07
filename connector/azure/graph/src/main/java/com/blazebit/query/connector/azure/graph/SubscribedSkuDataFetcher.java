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

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.microsoft.graph.beta.models.SubscribedSku;
import com.microsoft.graph.beta.serviceclient.GraphServiceClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martijn Sprengers
 * @since 1.0.0
 */
public class SubscribedSkuDataFetcher implements DataFetcher<SubscribedSku>, Serializable {

    public static final SubscribedSkuDataFetcher INSTANCE = new SubscribedSkuDataFetcher();

    private SubscribedSkuDataFetcher() {
    }

    @Override
    public List<SubscribedSku> fetch(DataFetchContext context) {
        try {
            List<GraphServiceClient> graphServiceClients = AzureGraphConnectorConfig.GRAPH_SERVICE_CLIENT.getAll(context);
            List<SubscribedSku> list = new ArrayList<>();
            for (GraphServiceClient graphServiceClient : graphServiceClients) {
                list.addAll(graphServiceClient.subscribedSkus().get().getValue());
            }
            return list;
        } catch (RuntimeException e) {
            throw new DataFetcherException("Could not fetch subscribed sku list", e);
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.beansConvention(SubscribedSku.class, AzureGraphConventionContext.INSTANCE);
    }
}

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

import com.blazebit.query.spi.DataFetcherConfig;
import com.microsoft.graph.serviceclient.GraphServiceClient;

/**
 * Configuration properties for the Azure {@link com.blazebit.query.spi.DataFetcher} instances.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AzureGraphConnectorConfig {

    /**
     * Specified the {@link GraphServiceClient} to use for querying data.
     */
    public static final DataFetcherConfig<GraphServiceClient> GRAPH_SERVICE_CLIENT = DataFetcherConfig.forPropertyName( "azureGraphServiceClient" );

    private AzureGraphConnectorConfig() {
    }
}

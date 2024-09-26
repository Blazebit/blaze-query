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

import com.microsoft.graph.beta.models.ManagedDevice;
import java.util.Map;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;
import com.microsoft.graph.beta.models.Application;
import com.microsoft.graph.beta.models.ConditionalAccessPolicy;
import com.microsoft.graph.beta.models.User;

/**
 * The schema provider for the Azure Subscription connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AzureGraphSchemaProvider implements QuerySchemaProvider {
    /**
     * Creates a new schema provider.
     */
    public AzureGraphSchemaProvider() {
    }

    @Override
    public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
        return Map.<Class<?>, DataFetcher<?>>of(
                Application.class, ApplicationDataFetcher.INSTANCE,
                ConditionalAccessPolicy.class, ConditionalAccessPolicyDataFetcher.INSTANCE,
                ManagedDevice.class, ManagedDeviceDataFetcher.INSTANCE,
                User.class, UserDataFetcher.INSTANCE
        );
    }
}

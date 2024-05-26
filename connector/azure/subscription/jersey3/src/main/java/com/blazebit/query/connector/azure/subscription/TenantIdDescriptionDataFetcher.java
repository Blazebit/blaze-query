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

package com.blazebit.query.connector.azure.subscription;

import java.io.Serializable;
import java.util.List;

import com.blazebit.query.connector.azure.invoker.ApiException;
import com.blazebit.query.connector.azure.subscription.api.TenantsApi;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class TenantIdDescriptionDataFetcher implements DataFetcher, Serializable {

    public static final TenantIdDescriptionDataFetcher INSTANCE = new TenantIdDescriptionDataFetcher();

    private TenantIdDescriptionDataFetcher() {
    }

    @Override
    public List<?> fetch(DataFetchContext context) {
        try {
            return new TenantsApi( AzureConnectorConfig.API_CLIENT.get( context ) )
                    .tenantsList( "2022-12-01" ).getValue();
        } catch (ApiException e) {
            throw new RuntimeException( "Could not fetch tenant list", e );
        }
    }
}

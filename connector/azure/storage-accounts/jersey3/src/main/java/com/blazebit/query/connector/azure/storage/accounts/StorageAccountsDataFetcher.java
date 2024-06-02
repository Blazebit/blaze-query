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

package com.blazebit.query.connector.azure.storage.accounts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.azure.invoker.ApiClient;
import com.blazebit.query.connector.azure.invoker.ApiException;
import com.blazebit.query.connector.azure.storage.accounts.api.StorageAccountsApi;
import com.blazebit.query.connector.azure.storage.accounts.model.StorageAccount;
import com.blazebit.query.connector.azure.storage.accounts.model.StorageAccountListResult;
import com.blazebit.query.connector.azure.subscription.AzureConnectorConfig;
import com.blazebit.query.connector.azure.subscription.api.SubscriptionsApi;
import com.blazebit.query.connector.azure.subscription.model.Subscription;
import com.blazebit.query.connector.azure.subscription.model.SubscriptionListResult;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetchContext;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class StorageAccountsDataFetcher implements DataFetcher<StorageAccount>, Serializable {

    public static final StorageAccountsDataFetcher INSTANCE = new StorageAccountsDataFetcher();

    private StorageAccountsDataFetcher() {
    }

    @Override
    public List<StorageAccount> fetch(DataFetchContext context) {
        try {
            ApiClient apiClient = AzureConnectorConfig.API_CLIENT.get( context );
            SubscriptionListResult subscriptionListResult = new SubscriptionsApi( apiClient ).subscriptionsList( "2022-12-01" );
            StorageAccountsApi storageAccountsApi = new StorageAccountsApi( apiClient );
            List<StorageAccount> list = new ArrayList<>();
            for ( Subscription subscription : subscriptionListResult.getValue() ) {
                StorageAccountListResult storageAccountListResult = storageAccountsApi.storageAccountsList(
                        "2023-05-01",
                        subscription.getSubscriptionId()
                );
                list.addAll(storageAccountListResult.getValue());
            }
            return list;
        } catch (ApiException e) {
            throw new RuntimeException( "Could not fetch virtual machine list", e );
        }
    }
}

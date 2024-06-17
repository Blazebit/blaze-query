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

package com.blazebit.query.connector.azure.storage.accounts.v20230501;

import com.blazebit.query.spi.DataFetcherException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.azure.base.AzureConnectorConfig;
import com.blazebit.query.connector.azure.base.invoker.ApiClient;
import com.blazebit.query.connector.azure.base.invoker.ApiException;
import com.blazebit.query.connector.azure.storage.accounts.v20230501.api.StorageAccountsApi;
import com.blazebit.query.connector.azure.storage.accounts.v20230501.model.StorageAccount;
import com.blazebit.query.connector.azure.storage.accounts.v20230501.model.StorageAccountListResult;
import com.blazebit.query.connector.azure.subscription.v20221201.model.Subscription;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class StorageAccountsDataFetcher implements DataFetcher<StorageAccount>, Serializable {

    public static final StorageAccountsDataFetcher INSTANCE = new StorageAccountsDataFetcher();

    private StorageAccountsDataFetcher() {
    }

    @Override
    public List<StorageAccount> fetch(DataFetchContext context) throws DataFetcherException {
        try {
            ApiClient apiClient = AzureConnectorConfig.API_CLIENT.get( context );
            StorageAccountsApi storageAccountsApi = new StorageAccountsApi( apiClient );
            List<StorageAccount> list = new ArrayList<>();
            for ( Subscription subscription : context.getSession().getOrFetch( Subscription.class ) ) {
                StorageAccountListResult storageAccountListResult = storageAccountsApi.storageAccountsList(
                        "2023-05-01",
                        subscription.getSubscriptionId()
                );
                list.addAll(storageAccountListResult.getValue());
            }
            return list;
        } catch (ApiException e) {
            throw new DataFetcherException( "Could not fetch virtual machine list", e );
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.beansConvention( StorageAccount.class );
    }
}

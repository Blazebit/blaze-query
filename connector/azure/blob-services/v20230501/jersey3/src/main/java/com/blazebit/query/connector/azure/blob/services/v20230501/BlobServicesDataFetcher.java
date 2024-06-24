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

package com.blazebit.query.connector.azure.blob.services.v20230501;

import com.blazebit.query.spi.DataFetcherException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.azure.base.AzureConnectorConfig;
import com.blazebit.query.connector.azure.base.invoker.ApiClient;
import com.blazebit.query.connector.azure.base.invoker.ApiException;
import com.blazebit.query.connector.azure.blob.services.v20230501.api.BlobServiceApi;
import com.blazebit.query.connector.azure.blob.services.v20230501.model.BlobServiceItems;
import com.blazebit.query.connector.azure.blob.services.v20230501.model.BlobServiceProperties;
import com.blazebit.query.connector.azure.storage.accounts.v20230501.model.StorageAccount;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class BlobServicesDataFetcher implements DataFetcher<BlobServiceProperties>, Serializable {

    public static final BlobServicesDataFetcher INSTANCE = new BlobServicesDataFetcher();

    private BlobServicesDataFetcher() {
    }

    @Override
    public List<BlobServiceProperties> fetch(DataFetchContext context) throws DataFetcherException {
        try {
            ApiClient apiClient = AzureConnectorConfig.API_CLIENT.get( context );
            BlobServiceApi blobServiceApi = new BlobServiceApi( apiClient );
            List<BlobServiceProperties> list = new ArrayList<>();
            for ( StorageAccount storageAccount : context.getSession().getOrFetch(StorageAccount.class) ) {
                // Format: /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/{resourceProviderNamespace}/{resourceType}/{resourceName}
                String[] splitParts = storageAccount.getId().split( "/" );
                assert splitParts.length == 8;
                String subscriptionId = splitParts[1];
                String resourceGroupName = splitParts[3];
                String storageAccountName = splitParts[7];
                BlobServiceItems blobServiceItems = blobServiceApi.blobServicesList(
                        resourceGroupName,
                        storageAccountName,
                        "2023-05-01",
                        subscriptionId
                );
                list.addAll(blobServiceItems.getValue());
            }
            return list;
        } catch (ApiException e) {
            throw new DataFetcherException( "Could not fetch virtual machine list", e );
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.beansConvention( BlobServiceProperties.class );
    }
}

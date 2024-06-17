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

package com.blazebit.query.connector.azure.virtual.machine.v20240301;

import com.blazebit.query.spi.DataFetcherException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.azure.base.AzureConnectorConfig;
import com.blazebit.query.connector.azure.base.invoker.ApiClient;
import com.blazebit.query.connector.azure.base.invoker.ApiException;
import com.blazebit.query.connector.azure.subscription.v20221201.model.Subscription;
import com.blazebit.query.connector.azure.virtual.machine.v20240301.api.VirtualMachinesApi;
import com.blazebit.query.connector.azure.virtual.machine.v20240301.model.VirtualMachine;
import com.blazebit.query.connector.azure.virtual.machine.v20240301.model.VirtualMachineListResult;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class VirtualMachineDataFetcher implements DataFetcher<VirtualMachine>, Serializable {

    public static final VirtualMachineDataFetcher INSTANCE = new VirtualMachineDataFetcher();

    private VirtualMachineDataFetcher() {
    }

    @Override
    public List<VirtualMachine> fetch(DataFetchContext context) throws DataFetcherException {
        try {
            ApiClient apiClient = AzureConnectorConfig.API_CLIENT.get( context );
            VirtualMachinesApi virtualMachinesApi = new VirtualMachinesApi( apiClient );
            List<VirtualMachine> list = new ArrayList<>();
            for ( Subscription subscription : context.getSession().getOrFetch( Subscription.class ) ) {
                VirtualMachineListResult virtualMachineListResult = virtualMachinesApi.virtualMachinesListAll(
                        "2024-03-01",
                        subscription.getSubscriptionId(),
                        null,
                        null,
                        null
                );
                list.addAll(virtualMachineListResult.getValue());
            }
            return list;
        } catch (ApiException e) {
            throw new DataFetcherException( "Could not fetch virtual machine list", e );
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.beansConvention( VirtualMachine.class );
    }
}

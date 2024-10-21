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

package com.blazebit.query.connector.kandji;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.kandji.api.DeviceInformationApi;
import com.blazebit.query.connector.kandji.invoker.ApiClient;
import com.blazebit.query.connector.kandji.invoker.ApiException;
import com.blazebit.query.connector.kandji.model.GetDeviceDetails200Response;
import com.blazebit.query.connector.kandji.model.ListDevices200ResponseInner;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DeviceDetailDataFetcher implements DataFetcher<GetDeviceDetails200Response>, Serializable {

    public static final DeviceDetailDataFetcher INSTANCE = new DeviceDetailDataFetcher();

    private DeviceDetailDataFetcher() {
    }

    @Override
    public List<GetDeviceDetails200Response> fetch(DataFetchContext context) {
        try {
            List<ApiClient> apiClients = KandjiConnectorConfig.API_CLIENT.getAll(context);
            List<GetDeviceDetails200Response> list = new ArrayList<>();
            List<? extends ListDevices200ResponseInner> devices = context.getSession().getOrFetch(ListDevices200ResponseInner.class);
            for (ApiClient apiClient : apiClients) {
                DeviceInformationApi deviceInformationApi = new DeviceInformationApi(apiClient);
                for (ListDevices200ResponseInner device : devices) {
                    list.add(deviceInformationApi.getDeviceDetails(device.getDeviceId()));
                }
            }
            return list;
        } catch (ApiException e) {
            throw new DataFetcherException("Could not fetch device details list", e);
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.beansConvention(GetDeviceDetails200Response.class, KandjiConventionContext.INSTANCE);
    }
}

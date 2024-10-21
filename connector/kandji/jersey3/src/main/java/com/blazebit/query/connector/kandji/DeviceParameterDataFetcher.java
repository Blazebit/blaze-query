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
import com.blazebit.query.connector.kandji.model.GetDeviceParameters200ResponseParametersInner;
import com.blazebit.query.connector.kandji.model.ListDevices200ResponseInner;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DeviceParameterDataFetcher implements DataFetcher<DeviceParameter>, Serializable {

    public static final DeviceParameterDataFetcher INSTANCE = new DeviceParameterDataFetcher();

    private DeviceParameterDataFetcher() {
    }

    @Override
    public List<DeviceParameter> fetch(DataFetchContext context) {
        try {
            List<ApiClient> apiClients = KandjiConnectorConfig.API_CLIENT.getAll( context);
            List<DeviceParameter> list = new ArrayList<>();
            List<? extends ListDevices200ResponseInner> devices = context.getSession().getOrFetch(ListDevices200ResponseInner.class);
            for (ApiClient apiClient : apiClients) {
                DeviceInformationApi deviceInformationApi = new DeviceInformationApi(apiClient);
                for ( ListDevices200ResponseInner device : devices ) {
                    List<GetDeviceParameters200ResponseParametersInner> parameters = deviceInformationApi.getDeviceParameters(device.getDeviceId()).getParameters();
                    if ( parameters != null ) {
                        for ( GetDeviceParameters200ResponseParametersInner parameter : parameters ) {
                            list.add( new DeviceParameter( device.getDeviceId(), parameter ) );
                        }
                    }
                }
            }
            return list;
        } catch (ApiException e) {
            throw new DataFetcherException("Could not fetch device parameter list", e);
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.beansConvention(DeviceParameter.class, KandjiConventionContext.INSTANCE);
    }
}

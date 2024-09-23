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

import java.util.Map;

import com.blazebit.query.connector.kandji.model.GetDeviceDetails200Response;
import com.blazebit.query.connector.kandji.model.ListDevices200ResponseInner;
import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

/**
 * The schema provider for the Kandji connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class KandjiSchemaProvider implements QuerySchemaProvider {
    /**
     * Creates a new schema provider.
     */
    public KandjiSchemaProvider() {
    }

    @Override
    public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
        return Map.<Class<?>, DataFetcher<?>>of(
                ListDevices200ResponseInner.class, DeviceDataFetcher.INSTANCE,
                DeviceParameter.class, DeviceParameterDataFetcher.INSTANCE,
                GetDeviceDetails200Response.class, DeviceDetailDataFetcher.INSTANCE
        );
    }
}

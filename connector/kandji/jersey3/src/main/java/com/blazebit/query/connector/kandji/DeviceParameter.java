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

import com.blazebit.query.connector.kandji.model.GetDeviceParameters200ResponseParametersInner;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DeviceParameter extends GetDeviceParameters200ResponseParametersInner {

    private final String deviceId;

    /**
     * Creates a new DeviceParameter.
     *
     * @param deviceId The device id
     * @param parameters The device parameters
     */
    public DeviceParameter(String deviceId, GetDeviceParameters200ResponseParametersInner parameters) {
        this.deviceId = deviceId;
        setCategory( parameters.getCategory() );
        setItemId( parameters.getItemId() );
        setName( parameters.getName() );
        setStatus( parameters.getStatus() );
        setSubcategory( parameters.getSubcategory() );
    }

    /**
     * Returns the device id.
     *
     * @return the device id
     */
    public String getDeviceId() {
        return deviceId;
    }
}

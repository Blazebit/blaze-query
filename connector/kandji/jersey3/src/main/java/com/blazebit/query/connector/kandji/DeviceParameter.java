/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
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

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
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

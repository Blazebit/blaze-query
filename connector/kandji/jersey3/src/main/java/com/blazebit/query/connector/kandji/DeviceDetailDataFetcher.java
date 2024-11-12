/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
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
			List<ApiClient> apiClients = KandjiConnectorConfig.API_CLIENT.getAll( context );
			List<GetDeviceDetails200Response> list = new ArrayList<>();
			List<? extends ListDevices200ResponseInner> devices = context.getSession()
					.getOrFetch( ListDevices200ResponseInner.class );
			for ( ApiClient apiClient : apiClients ) {
				DeviceInformationApi deviceInformationApi = new DeviceInformationApi( apiClient );
				for ( ListDevices200ResponseInner device : devices ) {
					list.add( deviceInformationApi.getDeviceDetails( device.getDeviceId() ) );
				}
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch device details list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GetDeviceDetails200Response.class, KandjiConventionContext.INSTANCE );
	}
}

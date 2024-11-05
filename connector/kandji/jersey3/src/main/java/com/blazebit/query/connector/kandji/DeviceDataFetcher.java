/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.kandji;

import com.blazebit.query.connector.kandji.api.DeviceInformationApi;
import com.blazebit.query.connector.kandji.invoker.ApiClient;
import com.blazebit.query.connector.kandji.invoker.ApiException;
import com.blazebit.query.connector.kandji.model.ListDevices200ResponseInner;
import com.blazebit.query.spi.DataFetcherException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DeviceDataFetcher implements DataFetcher<ListDevices200ResponseInner>, Serializable {

	public static final DeviceDataFetcher INSTANCE = new DeviceDataFetcher();

	private DeviceDataFetcher() {
	}

	@Override
	public List<ListDevices200ResponseInner> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = KandjiConnectorConfig.API_CLIENT.getAll( context );
			List<ListDevices200ResponseInner> list = new ArrayList<>();
			for ( ApiClient apiClient : apiClients ) {
				DeviceInformationApi deviceInformationApi = new DeviceInformationApi( apiClient );
				for ( int offset = 0; ; offset += 300 ) {
					List<ListDevices200ResponseInner> devices = deviceInformationApi.listDevices(
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							"300",
							Integer.toString( offset )
					);
					list.addAll( devices );
					if ( devices.size() != 300 ) {
						break;
					}
				}
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch device list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( ListDevices200ResponseInner.class, KandjiConventionContext.INSTANCE );
	}
}

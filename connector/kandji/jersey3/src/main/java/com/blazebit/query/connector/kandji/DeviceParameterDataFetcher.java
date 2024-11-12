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
			List<ApiClient> apiClients = KandjiConnectorConfig.API_CLIENT.getAll( context );
			List<DeviceParameter> list = new ArrayList<>();
			List<? extends ListDevices200ResponseInner> devices = context.getSession()
					.getOrFetch( ListDevices200ResponseInner.class );
			for ( ApiClient apiClient : apiClients ) {
				DeviceInformationApi deviceInformationApi = new DeviceInformationApi( apiClient );
				for ( ListDevices200ResponseInner device : devices ) {
					List<GetDeviceParameters200ResponseParametersInner> parameters = deviceInformationApi.getDeviceParameters(
							device.getDeviceId() ).getParameters();
					if ( parameters != null ) {
						for ( GetDeviceParameters200ResponseParametersInner parameter : parameters ) {
							list.add( new DeviceParameter( device.getDeviceId(), parameter ) );
						}
					}
				}
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch device parameter list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( DeviceParameter.class, KandjiConventionContext.INSTANCE );
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.workspace.endpointverification;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.services.directory.Directory;
import com.google.api.services.directory.model.ChromeOsDevice;
import com.google.api.services.directory.model.ChromeOsDevices;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches ChromeOS devices enrolled in Google Workspace Endpoint Verification.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class ChromeOsDeviceDataFetcher implements DataFetcher<GoogleChromeOsDevice>, Serializable {

	public static final ChromeOsDeviceDataFetcher INSTANCE = new ChromeOsDeviceDataFetcher();

	private static final String CUSTOMER_ID = "my_customer";

	private ChromeOsDeviceDataFetcher() {
	}

	@Override
	public List<GoogleChromeOsDevice> fetch(DataFetchContext context) {
		try {
			List<Directory> directoryServices =
					GoogleWorkspaceEndpointVerificationConnectorConfig.GOOGLE_DIRECTORY_SERVICE.getAll( context );
			List<GoogleChromeOsDevice> list = new ArrayList<>();
			for ( Directory directory : directoryServices ) {
				String pageToken = null;
				do {
					Directory.Chromeosdevices.List request = directory.chromeosdevices()
							.list( CUSTOMER_ID )
							.setPageToken( pageToken );
					ChromeOsDevices response = request.execute();
					List<ChromeOsDevice> devices = response.getChromeosdevices();
					if ( devices != null ) {
						for ( ChromeOsDevice device : devices ) {
							list.add( new GoogleChromeOsDevice( device.getDeviceId(), device ) );
						}
					}
					pageToken = response.getNextPageToken();
				}
				while ( pageToken != null );
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch ChromeOS device list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention(
				GoogleChromeOsDevice.class,
				GoogleWorkspaceEndpointVerificationConventionContext.INSTANCE );
	}
}

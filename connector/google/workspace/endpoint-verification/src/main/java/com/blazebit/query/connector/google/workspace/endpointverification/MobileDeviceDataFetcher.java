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
import com.google.api.services.directory.model.MobileDevice;
import com.google.api.services.directory.model.MobileDevices;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches mobile devices (Android/iOS) enrolled in Google Workspace Endpoint Verification.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class MobileDeviceDataFetcher implements DataFetcher<GoogleMobileDevice>, Serializable {

	public static final MobileDeviceDataFetcher INSTANCE = new MobileDeviceDataFetcher();

	private static final String CUSTOMER_ID = "my_customer";

	private MobileDeviceDataFetcher() {
	}

	@Override
	public List<GoogleMobileDevice> fetch(DataFetchContext context) {
		try {
			List<Directory> directoryServices =
					GoogleWorkspaceEndpointVerificationConnectorConfig.GOOGLE_DIRECTORY_SERVICE.getAll( context );
			List<GoogleMobileDevice> list = new ArrayList<>();
			for ( Directory directory : directoryServices ) {
				String pageToken = null;
				do {
					Directory.Mobiledevices.List request = directory.mobiledevices()
							.list( CUSTOMER_ID )
							.setPageToken( pageToken );
					MobileDevices response = request.execute();
					List<MobileDevice> devices = response.getMobiledevices();
					if ( devices != null ) {
						for ( MobileDevice device : devices ) {
							list.add( new GoogleMobileDevice( CUSTOMER_ID, device ) );
						}
					}
					pageToken = response.getNextPageToken();
				}
				while ( pageToken != null );
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch mobile device list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention(
				GoogleMobileDevice.class,
				GoogleWorkspaceEndpointVerificationConventionContext.INSTANCE );
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.drive;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DriveDataFetcher implements DataFetcher<com.google.api.services.drive.model.Drive>, Serializable {

	public static final DriveDataFetcher INSTANCE = new DriveDataFetcher();

	private DriveDataFetcher() {
	}

	@Override
	public List<com.google.api.services.drive.model.Drive> fetch(DataFetchContext context) {
		try {
			List<Drive> directoryServices = GoogleDriveConnectorConfig.GOOGLE_DRIVE_SERVICE.getAll( context );
			List<com.google.api.services.drive.model.Drive> list = new ArrayList<>();
			OUTER: for ( Drive drive : directoryServices ) {
				try {
					list.addAll( drive.drives().list().execute().getDrives() );
				}
				catch ( GoogleJsonResponseException e ) {
					for ( GoogleJsonError.Details detail : e.getDetails().getDetails() ) {
						if ( "SERVICE_DISABLED".equals( detail.getReason() ) ) {
							continue OUTER;
						}
					}
					throw e;
				}
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch drive list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( com.google.api.services.drive.model.Drive.class, GoogleDriveConventionContext.INSTANCE );
	}
}

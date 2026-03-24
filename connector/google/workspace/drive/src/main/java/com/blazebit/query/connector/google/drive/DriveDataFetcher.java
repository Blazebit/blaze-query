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
import com.google.api.services.drive.model.DriveList;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DriveDataFetcher implements DataFetcher<GoogleDrive>, Serializable {

	private static final Logger LOG = Logger.getLogger( DriveDataFetcher.class.getName() );

	public static final DriveDataFetcher INSTANCE = new DriveDataFetcher();

	private DriveDataFetcher() {
	}

	@Override
	public List<GoogleDrive> fetch(DataFetchContext context) {
		try {
			List<Drive> directoryServices = GoogleDriveConnectorConfig.GOOGLE_DRIVE_SERVICE.getAll( context );
			List<GoogleDrive> list = new ArrayList<>();
			OUTER: for ( Drive drive : directoryServices ) {
				try {
					String pageToken = null;
					do {
						DriveList result = drive.drives().list()
								.setPageToken( pageToken )
								.execute();
						List<com.google.api.services.drive.model.Drive> drives = result.getDrives();
						if ( drives != null ) {
							for ( com.google.api.services.drive.model.Drive d : drives ) {
								list.add( new GoogleDrive( d.getId(), d ) );
							}
						}
						pageToken = result.getNextPageToken();
					}
					while ( pageToken != null );
				}
				catch ( GoogleJsonResponseException e ) {
					GoogleJsonError error = e.getDetails();
					if ( error != null && error.getDetails() != null ) {
						for ( GoogleJsonError.Details detail : error.getDetails() ) {
							if ( "SERVICE_DISABLED".equals( detail.getReason() ) ) {
								LOG.log( Level.WARNING,
										"Google Drive API is not enabled, skipping drive fetch." );
								continue OUTER;
							}
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
		return DataFormats.beansConvention( GoogleDrive.class, GoogleDriveConventionContext.INSTANCE );
	}
}

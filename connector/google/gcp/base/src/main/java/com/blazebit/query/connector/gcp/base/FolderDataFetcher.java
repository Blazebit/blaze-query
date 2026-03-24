/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.base;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.cloud.resourcemanager.v3.Folder;
import com.google.cloud.resourcemanager.v3.FoldersClient;
import com.google.cloud.resourcemanager.v3.FoldersSettings;

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
public class FolderDataFetcher implements DataFetcher<GcpFolder>, Serializable {

	private static final Logger LOG = Logger.getLogger( FolderDataFetcher.class.getName() );

	public static final FolderDataFetcher INSTANCE = new FolderDataFetcher();

	private FolderDataFetcher() {
	}

	@Override
	public List<GcpFolder> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<GcpFolder> list = new ArrayList<>();
			List<? extends GcpOrganization> organizations = context.getSession().getOrFetch( GcpOrganization.class );

			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				for ( GcpOrganization organization : organizations ) {
					final FoldersSettings settings = FoldersSettings.newBuilder()
							.setCredentialsProvider( credentialsProvider )
							.build();
					try (FoldersClient client = FoldersClient.create( settings )) {
						try {
							final FoldersClient.ListFoldersPagedResponse response = client.listFolders( organization.getPayload().getName() );
							for ( Folder instance : response.iterateAll() ) {
								list.add( new GcpFolder( instance.getName(), instance ) );
							}
						}
						catch (PermissionDeniedException e) {
							if ( isServiceDisabled( e ) ) {
								LOG.log( Level.WARNING,
										"Resource Manager API is not enabled for organization ''{0}'', skipping folder fetch.",
										organization.getPayload().getName() );
								continue;
							}
							throw e;
						}
					}
				}
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch folder list", e );
		}
	}

	private static boolean isServiceDisabled(PermissionDeniedException e) {
		var details = e.getErrorDetails();
		if ( details != null && details.getErrorInfo() != null ) {
			return "SERVICE_DISABLED".equals( details.getErrorInfo().getReason() );
		}
		return false;
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GcpFolder.class, GcpConventionContext.INSTANCE );
	}
}

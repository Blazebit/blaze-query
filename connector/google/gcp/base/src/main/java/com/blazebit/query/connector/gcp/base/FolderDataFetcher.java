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
import com.google.cloud.resourcemanager.v3.Folder;
import com.google.cloud.resourcemanager.v3.FoldersClient;
import com.google.cloud.resourcemanager.v3.FoldersSettings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class FolderDataFetcher implements DataFetcher<GcpFolder>, Serializable {

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
						final FoldersClient.ListFoldersPagedResponse response = client.listFolders( organization.getPayload().getName() );
						for ( Folder instance : response.iterateAll() ) {
							list.add( new GcpFolder( instance.getName(), instance ) );
						}
					}
				}
			}
			return list;
		}
		catch (Exception e) {
			throw new DataFetcherException( "Could not fetch folder list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GcpFolder.class, GcpConventionContext.INSTANCE );
	}
}

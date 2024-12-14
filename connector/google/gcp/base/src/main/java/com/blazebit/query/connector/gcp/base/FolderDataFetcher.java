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
import com.google.cloud.resourcemanager.v3.Organization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class FolderDataFetcher implements DataFetcher<Folder>, Serializable {

	public static final FolderDataFetcher INSTANCE = new FolderDataFetcher();

	private FolderDataFetcher() {
	}

	@Override
	public List<Folder> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<Folder> list = new ArrayList<>();
			List<? extends Organization> organizations = context.getSession().getOrFetch( Organization.class );

			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				for ( Organization organization : organizations ) {
					final FoldersSettings settings = FoldersSettings.newBuilder()
							.setCredentialsProvider( credentialsProvider )
							.build();
					try (FoldersClient client = FoldersClient.create( settings )) {
						final FoldersClient.ListFoldersPagedResponse response = client.listFolders( organization.getName() );
						for ( Folder instance : response.iterateAll() ) {
							list.add( instance );
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
		return DataFormats.beansConvention( Folder.class, GcpConventionContext.INSTANCE );
	}
}

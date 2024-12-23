/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.storage;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.gcp.base.GcpConnectorConfig;
import com.blazebit.query.connector.gcp.base.GcpConventionContext;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.resourcemanager.v3.Project;
import com.google.storage.v2.Bucket;
import com.google.storage.v2.StorageClient;
import com.google.storage.v2.StorageSettings;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class BucketDataFetcher implements DataFetcher<Bucket>, Serializable {

	public static final BucketDataFetcher INSTANCE = new BucketDataFetcher();

	private BucketDataFetcher() {
	}

	@Override
	public List<Bucket> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<Bucket> list = new ArrayList<>();
			List<? extends Project> projects = context.getSession().getOrFetch( Project.class );
			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final StorageSettings settings = StorageSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build();
				try (StorageClient client = StorageClient.create( settings )) {
					for ( Project project : projects ) {
						final StorageClient.ListBucketsPagedResponse response = client.listBuckets( project.getName() );
						for ( Bucket instance : response.iterateAll() ) {
							list.add( instance );
						}
					}
				}
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch bucket list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( Bucket.class, GcpConventionContext.INSTANCE );
	}
}

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
import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.AssetServiceClient;
import com.google.cloud.asset.v1.AssetServiceSettings;
import com.google.cloud.resourcemanager.v3.Folder;
import com.google.cloud.resourcemanager.v3.Organization;
import com.google.cloud.resourcemanager.v3.Project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AssetDataFetcher implements DataFetcher<Asset>, Serializable {

	public static final AssetDataFetcher INSTANCE = new AssetDataFetcher();

	private AssetDataFetcher() {
	}

	@Override
	public List<Asset> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<Asset> list = new ArrayList<>();
			List<? extends Organization> organizations = context.getSession().getOrFetch( Organization.class );
			List<? extends Folder> folders = context.getSession().getOrFetch( Folder.class );
			List<? extends Project> projects = context.getSession().getOrFetch( Project.class );

			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final AssetServiceSettings settings = AssetServiceSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build();
				try (AssetServiceClient client = AssetServiceClient.create( settings )) {
					for ( Organization organization : organizations ) {
						final AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(
								organization.getName()
						);
						for ( Asset instance : response.iterateAll() ) {
							list.add( instance );
						}
					}
					for ( Folder folder : folders ) {
						final AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(
								folder.getName()
						);
						for ( Asset instance : response.iterateAll() ) {
							list.add( instance );
						}
					}
					for ( Project project : projects ) {
						final AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(
								project.getName()
						);
						for ( Asset instance : response.iterateAll() ) {
							list.add( instance );
						}
					}
				}
			}
			return list;
		}
		catch (Exception e) {
			throw new DataFetcherException( "Could not fetch asset list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( Asset.class, GcpConventionContext.INSTANCE );
	}
}

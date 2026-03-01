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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AssetDataFetcher implements DataFetcher<GcpAsset>, Serializable {

	public static final AssetDataFetcher INSTANCE = new AssetDataFetcher();

	private AssetDataFetcher() {
	}

	@Override
	public List<GcpAsset> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<GcpAsset> list = new ArrayList<>();
			List<? extends GcpOrganization> organizations = context.getSession().getOrFetch( GcpOrganization.class );
			List<? extends GcpFolder> folders = context.getSession().getOrFetch( GcpFolder.class );
			List<? extends GcpProject> projects = context.getSession().getOrFetch( GcpProject.class );

			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final AssetServiceSettings settings = AssetServiceSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build();
				try (AssetServiceClient client = AssetServiceClient.create( settings )) {
					for ( GcpOrganization organization : organizations ) {
						final AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(
								organization.getPayload().getName()
						);
						for ( Asset instance : response.iterateAll() ) {
							list.add( new GcpAsset( instance.getName(), instance ) );
						}
					}
					for ( GcpFolder folder : folders ) {
						final AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(
								folder.getPayload().getName()
						);
						for ( Asset instance : response.iterateAll() ) {
							list.add( new GcpAsset( instance.getName(), instance ) );
						}
					}
					for ( GcpProject project : projects ) {
						final AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(
								project.getPayload().getName()
						);
						for ( Asset instance : response.iterateAll() ) {
							list.add( new GcpAsset( instance.getName(), instance ) );
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
		return DataFormats.beansConvention( GcpAsset.class, GcpConventionContext.INSTANCE );
	}
}

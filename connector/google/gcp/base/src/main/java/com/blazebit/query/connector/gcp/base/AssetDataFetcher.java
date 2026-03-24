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
import com.google.cloud.asset.v1.Asset;
import com.google.cloud.asset.v1.AssetServiceClient;
import com.google.cloud.asset.v1.AssetServiceSettings;

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
public class AssetDataFetcher implements DataFetcher<GcpAsset>, Serializable {

	private static final Logger LOG = Logger.getLogger( AssetDataFetcher.class.getName() );

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
						try {
							final AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(
									organization.getPayload().getName()
							);
							for ( Asset instance : response.iterateAll() ) {
								list.add( new GcpAsset( instance.getName(), instance ) );
							}
						}
						catch (PermissionDeniedException e) {
							if ( isServiceDisabled( e ) ) {
								LOG.log( Level.WARNING,
										"Cloud Asset API is not enabled for organization ''{0}'', skipping asset fetch.",
										organization.getPayload().getName() );
								continue;
							}
							throw e;
						}
					}
					for ( GcpFolder folder : folders ) {
						try {
							final AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(
									folder.getPayload().getName()
							);
							for ( Asset instance : response.iterateAll() ) {
								list.add( new GcpAsset( instance.getName(), instance ) );
							}
						}
						catch (PermissionDeniedException e) {
							if ( isServiceDisabled( e ) ) {
								LOG.log( Level.WARNING,
										"Cloud Asset API is not enabled for folder ''{0}'', skipping asset fetch.",
										folder.getPayload().getName() );
								continue;
							}
							throw e;
						}
					}
					for ( GcpProject project : projects ) {
						try {
							final AssetServiceClient.ListAssetsPagedResponse response = client.listAssets(
									project.getPayload().getName()
							);
							for ( Asset instance : response.iterateAll() ) {
								list.add( new GcpAsset( instance.getName(), instance ) );
							}
						}
						catch (PermissionDeniedException e) {
							if ( isServiceDisabled( e ) ) {
								LOG.log( Level.WARNING,
										"Cloud Asset API is not enabled for project ''{0}'', skipping asset fetch.",
										project.getPayload().getProjectId() );
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
			throw new DataFetcherException( "Could not fetch asset list", e );
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
		return DataFormats.beansConvention( GcpAsset.class, GcpConventionContext.INSTANCE );
	}
}

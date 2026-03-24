/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.kms;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.gcp.base.GcpConnectorConfig;
import com.blazebit.query.connector.gcp.base.GcpConventionContext;
import com.blazebit.query.connector.gcp.base.GcpProject;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.client.http.HttpResponseException;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.cloud.kms.v1.CryptoKey;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyManagementServiceSettings;
import com.google.cloud.kms.v1.KeyRing;
import com.google.cloud.kms.v1.ListCryptoKeysRequest;
import com.google.cloud.kms.v1.ListKeyRingsRequest;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fetches Cloud KMS CryptoKeys for GCP projects.
 *
 * @author Martijn Sprengers
 * @since 2.3.0
 */
public class KmsCryptoKeyDataFetcher implements DataFetcher<GcpKmsCryptoKey>, Serializable {

	private static final Logger LOG = Logger.getLogger( KmsCryptoKeyDataFetcher.class.getName() );

	public static final KmsCryptoKeyDataFetcher INSTANCE = new KmsCryptoKeyDataFetcher();

	private KmsCryptoKeyDataFetcher() {
	}

	@Override
	public List<GcpKmsCryptoKey> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<GcpKmsCryptoKey> list = new ArrayList<>();
			List<? extends GcpProject> projects = context.getSession().getOrFetch( GcpProject.class );
			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final KeyManagementServiceSettings settings = KeyManagementServiceSettings.newBuilder()
						.setCredentialsProvider( credentialsProvider )
						.build();
				try (KeyManagementServiceClient client = KeyManagementServiceClient.create( settings )) {
					for ( GcpProject project : projects ) {
						try {
							ListKeyRingsRequest keyRingsRequest = ListKeyRingsRequest.newBuilder()
									.setParent( "projects/" + project.getPayload().getProjectId() + "/locations/-" )
									.build();
							for ( KeyRing keyRing : client.listKeyRings( keyRingsRequest ).iterateAll() ) {
								ListCryptoKeysRequest keysRequest = ListCryptoKeysRequest.newBuilder()
										.setParent( keyRing.getName() )
										.build();
								for ( CryptoKey cryptoKey : client.listCryptoKeys( keysRequest ).iterateAll() ) {
									list.add( new GcpKmsCryptoKey( cryptoKey.getName(), cryptoKey ) );
								}
							}
						}
						catch (PermissionDeniedException e) {
							if ( e.getCause() instanceof HttpResponseException httpEx
									&& httpEx.getContent() != null
									&& httpEx.getContent().contains( "\"accessNotConfigured\"" ) ) {
								LOG.log( Level.WARNING,
										"Cloud KMS API is not enabled for project ''{0}'', skipping crypto key fetch.",
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
			throw new DataFetcherException( "Could not fetch Cloud KMS crypto key list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GcpKmsCryptoKey.class, GcpConventionContext.INSTANCE );
	}
}

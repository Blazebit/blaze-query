/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.kms;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.KmsClientBuilder;
import software.amazon.awssdk.services.kms.model.GetKeyRotationStatusRequest;
import software.amazon.awssdk.services.kms.model.GetKeyRotationStatusResponse;
import software.amazon.awssdk.services.kms.model.KeyListEntry;
import software.amazon.awssdk.services.kms.model.KmsException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsKmsKeyRotationStatusDataFetcher implements DataFetcher<AwsKmsKeyRotationStatus>, Serializable {

	public static final AwsKmsKeyRotationStatusDataFetcher INSTANCE = new AwsKmsKeyRotationStatusDataFetcher();

	private AwsKmsKeyRotationStatusDataFetcher() {
	}

	@Override
	public List<AwsKmsKeyRotationStatus> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsKmsKeyRotationStatus> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					KmsClientBuilder kmsClientBuilder = KmsClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						kmsClientBuilder.httpClient( sdkHttpClient );
					}
					try (KmsClient client = kmsClientBuilder.build()) {
						for ( KeyListEntry key : client.listKeys().keys() ) {
							try {
								GetKeyRotationStatusResponse rotationStatus = client.getKeyRotationStatus(
										GetKeyRotationStatusRequest.builder()
												.keyId( key.keyId() )
												.build()
								);
								list.add( new AwsKmsKeyRotationStatus(
										account.getAccountId(),
										region.id(),
										key.keyId(),
										rotationStatus
								) );
							}
							catch (KmsException e) {
								// UnsupportedOperationException is thrown for keys that don't support rotation
								if ( Objects.equals( e.awsErrorDetails().errorCode(),
										"UnsupportedOperationException" ) ) {
									continue;
								}
								throw e;
							}
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch key rotation status list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsKmsKeyRotationStatus.class, AwsConventionContext.INSTANCE );
	}
}

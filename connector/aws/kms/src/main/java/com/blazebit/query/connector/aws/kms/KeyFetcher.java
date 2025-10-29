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
import software.amazon.awssdk.services.kms.model.DescribeKeyRequest;
import software.amazon.awssdk.services.kms.model.KeyListEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class KeyFetcher implements DataFetcher<AwsKey>, Serializable {

	public static final KeyFetcher INSTANCE = new KeyFetcher();

	private KeyFetcher() {
	}

	@Override
	public List<AwsKey> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsKey> list = new ArrayList<>();
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
							var describeKey = client.describeKey(
									DescribeKeyRequest.builder().keyId( key.keyId() ).build() );
							list.add( new AwsKey(
									account.getAccountId(),
									region.id(),
									key.keyId(),
									describeKey.keyMetadata()
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch key list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsKey.class, AwsConventionContext.INSTANCE );
	}
}

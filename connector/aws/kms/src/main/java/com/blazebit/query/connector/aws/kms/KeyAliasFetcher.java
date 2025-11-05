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
import software.amazon.awssdk.services.kms.model.AliasListEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class KeyAliasFetcher implements DataFetcher<AwsKeyAlias>, Serializable {

	public static final KeyAliasFetcher INSTANCE = new KeyAliasFetcher();

	private KeyAliasFetcher() {
	}

	@Override
	public List<AwsKeyAlias> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsKeyAlias> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					KmsClientBuilder kmsClientBuilder = KmsClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						kmsClientBuilder.httpClient( sdkHttpClient );
					}
					try (KmsClient client = kmsClientBuilder.build()) {
						for ( AliasListEntry aliasListEntry : client.listAliases().aliases() ) {
							list.add( new AwsKeyAlias(
									account.getAccountId(),
									region.id(),
									aliasListEntry.targetKeyId(),
									aliasListEntry )
							);
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch key alias list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsKeyAlias.class, AwsConventionContext.INSTANCE );
	}
}

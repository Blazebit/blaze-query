/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.backup;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.backup.BackupClient;
import software.amazon.awssdk.services.backup.BackupClientBuilder;
import software.amazon.awssdk.services.backup.model.ListProtectedResourcesRequest;
import software.amazon.awssdk.services.backup.model.ProtectedResource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class ProtectedResourceDataFetcher implements DataFetcher<AwsBackupProtectedResource>, Serializable {

	public static final ProtectedResourceDataFetcher INSTANCE = new ProtectedResourceDataFetcher();

	private ProtectedResourceDataFetcher() {
	}

	@Override
	public List<AwsBackupProtectedResource> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsBackupProtectedResource> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					BackupClientBuilder backupClientBuilder = BackupClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						backupClientBuilder.httpClient( sdkHttpClient );
					}
					try (BackupClient client = backupClientBuilder.build()) {
						for ( ProtectedResource protectedResource : client.listProtectedResourcesPaginator(
								ListProtectedResourcesRequest.builder().build() ).results() ) {
							list.add( new AwsBackupProtectedResource(
									protectedResource.resourceArn(),
									protectedResource
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch protected resource list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsBackupProtectedResource.class, AwsConventionContext.INSTANCE );
	}
}

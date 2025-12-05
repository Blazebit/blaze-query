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
import software.amazon.awssdk.services.backup.model.BackupVaultListMember;
import software.amazon.awssdk.services.backup.model.ListBackupVaultsRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class BackupVaultDataFetcher implements DataFetcher<AwsBackupVault>, Serializable {

	public static final BackupVaultDataFetcher INSTANCE = new BackupVaultDataFetcher();

	private BackupVaultDataFetcher() {
	}

	@Override
	public List<AwsBackupVault> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsBackupVault> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					BackupClientBuilder backupClientBuilder = BackupClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						backupClientBuilder.httpClient( sdkHttpClient );
					}
					try (BackupClient client = backupClientBuilder.build()) {
						for ( BackupVaultListMember backupVault : client.listBackupVaultsPaginator(
								ListBackupVaultsRequest.builder().build() ).backupVaultList() ) {
							list.add( new AwsBackupVault(
									backupVault.backupVaultArn(),
									backupVault
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch backup vault list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsBackupVault.class, AwsConventionContext.INSTANCE );
	}
}

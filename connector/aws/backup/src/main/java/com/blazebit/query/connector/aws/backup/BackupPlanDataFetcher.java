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
import software.amazon.awssdk.services.backup.model.BackupPlansListMember;
import software.amazon.awssdk.services.backup.model.GetBackupPlanRequest;
import software.amazon.awssdk.services.backup.model.GetBackupPlanResponse;
import software.amazon.awssdk.services.backup.model.ListBackupPlansRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class BackupPlanDataFetcher implements DataFetcher<AwsBackupPlan>, Serializable {

	public static final BackupPlanDataFetcher INSTANCE = new BackupPlanDataFetcher();

	private BackupPlanDataFetcher() {
	}

	@Override
	public List<AwsBackupPlan> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsBackupPlan> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					BackupClientBuilder backupClientBuilder = BackupClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						backupClientBuilder.httpClient( sdkHttpClient );
					}
					try (BackupClient client = backupClientBuilder.build()) {
						for ( BackupPlansListMember backupPlan : client.listBackupPlansPaginator(
								ListBackupPlansRequest.builder().build() ).backupPlansList() ) {
							GetBackupPlanRequest.Builder requestBuilder = GetBackupPlanRequest.builder()
									.backupPlanId( backupPlan.backupPlanId() );
							if ( backupPlan.versionId() != null ) {
								requestBuilder.versionId( backupPlan.versionId() );
							}
							GetBackupPlanResponse response = client.getBackupPlan( requestBuilder.build() );
							list.add( new AwsBackupPlan(
									response.backupPlanArn(),
									response
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch backup plan list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsBackupPlan.class, AwsConventionContext.INSTANCE );
	}
}

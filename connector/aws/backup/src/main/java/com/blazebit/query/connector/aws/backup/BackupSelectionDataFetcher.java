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
import software.amazon.awssdk.services.backup.model.BackupSelectionsListMember;
import software.amazon.awssdk.services.backup.model.GetBackupSelectionRequest;
import software.amazon.awssdk.services.backup.model.GetBackupSelectionResponse;
import software.amazon.awssdk.services.backup.model.ListBackupPlansRequest;
import software.amazon.awssdk.services.backup.model.ListBackupSelectionsRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class BackupSelectionDataFetcher implements DataFetcher<AwsBackupSelection>, Serializable {

	public static final BackupSelectionDataFetcher INSTANCE = new BackupSelectionDataFetcher();

	private BackupSelectionDataFetcher() {
	}

	@Override
	public List<AwsBackupSelection> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsBackupSelection> list = new ArrayList<>();
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
							ListBackupSelectionsRequest listBackupSelectionsRequest = ListBackupSelectionsRequest.builder()
									.backupPlanId( backupPlan.backupPlanId() )
									.build();
							for ( BackupSelectionsListMember backupSelection : client.listBackupSelectionsPaginator(
									listBackupSelectionsRequest ).backupSelectionsList() ) {
								GetBackupSelectionResponse backupSelectionResponse = client.getBackupSelection(
										GetBackupSelectionRequest.builder()
												.backupPlanId( backupPlan.backupPlanId() )
												.selectionId( backupSelection.selectionId() )
												.build()
								);
								list.add( new AwsBackupSelection(
										account.getAccountId(),
										region.id(),
										backupSelection.selectionId(),
										backupSelectionResponse
								) );
							}
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch backup selection list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsBackupSelection.class, AwsConventionContext.INSTANCE );
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.backup;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the AWS Backup connector.
 *
 * @author Donghwi Kim
 * @since 1.0.0
 */
public final class AwsBackupSchemaProvider implements QuerySchemaProvider {
	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				BackupVaultDataFetcher.INSTANCE,
				BackupPlanDataFetcher.INSTANCE,
				BackupSelectionDataFetcher.INSTANCE,
				RecoveryPointDataFetcher.INSTANCE,
				BackupJobDataFetcher.INSTANCE,
				ProtectedResourceDataFetcher.INSTANCE
		);
	}
}

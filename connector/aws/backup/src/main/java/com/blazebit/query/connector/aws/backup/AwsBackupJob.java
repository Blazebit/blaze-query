/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.backup;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.backup.model.BackupJob;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsBackupJob extends AwsWrapper<BackupJob> {

	public AwsBackupJob(String accountId, String regionId, String resourceId, BackupJob payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public BackupJob getPayload() {
		return super.getPayload();
	}
}

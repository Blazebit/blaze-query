/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.backup;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.backup.model.BackupVaultListMember;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsBackupVault extends AwsWrapper<BackupVaultListMember> {

	public AwsBackupVault(String arn, BackupVaultListMember payload) {
		super( arn, payload );
	}

	@Override
	public BackupVaultListMember getPayload() {
		return super.getPayload();
	}
}

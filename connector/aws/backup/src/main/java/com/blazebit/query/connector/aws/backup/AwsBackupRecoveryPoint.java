/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.backup;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.backup.model.RecoveryPointByBackupVault;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsBackupRecoveryPoint extends AwsWrapper<RecoveryPointByBackupVault> {

	public AwsBackupRecoveryPoint(String arn, RecoveryPointByBackupVault payload) {
		super( arn, payload );
	}

	@Override
	public RecoveryPointByBackupVault getPayload() {
		return super.getPayload();
	}
}

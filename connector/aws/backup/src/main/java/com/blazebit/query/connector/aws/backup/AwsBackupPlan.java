/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.backup;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.backup.model.GetBackupPlanResponse;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsBackupPlan extends AwsWrapper<GetBackupPlanResponse> {

	public AwsBackupPlan(String arn, GetBackupPlanResponse payload) {
		super( arn, payload );
	}

	@Override
	public GetBackupPlanResponse getPayload() {
		return super.getPayload();
	}
}

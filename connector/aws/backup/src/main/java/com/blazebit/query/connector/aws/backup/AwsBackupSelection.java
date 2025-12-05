/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.backup;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.backup.model.GetBackupSelectionResponse;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsBackupSelection extends AwsWrapper<GetBackupSelectionResponse> {

	public AwsBackupSelection(String accountId, String regionId, String resourceId, GetBackupSelectionResponse payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public GetBackupSelectionResponse getPayload() {
		return super.getPayload();
	}
}

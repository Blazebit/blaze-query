/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.kms;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.kms.model.GetKeyRotationStatusResponse;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsKmsKeyRotationStatus extends AwsWrapper<GetKeyRotationStatusResponse> {
	public AwsKmsKeyRotationStatus(String accountId, String region, String resourceId, GetKeyRotationStatusResponse payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public GetKeyRotationStatusResponse getPayload() {
		return super.getPayload();
	}
}

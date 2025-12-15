/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.cloudtrail;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.cloudtrail.model.GetTrailStatusResponse;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsCloudTrailTrailStatus extends AwsWrapper<GetTrailStatusResponse> {
	public AwsCloudTrailTrailStatus(String arn, GetTrailStatusResponse payload) {
		super( arn, payload );
	}

	@Override
	public GetTrailStatusResponse getPayload() {
		return super.getPayload();
	}
}

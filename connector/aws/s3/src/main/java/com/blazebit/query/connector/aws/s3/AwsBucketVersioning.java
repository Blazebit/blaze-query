/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.s3.model.GetBucketVersioningResponse;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsBucketVersioning extends AwsWrapper<GetBucketVersioningResponse> {
	public AwsBucketVersioning(String accountId, String region, String resourceId, GetBucketVersioningResponse payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public GetBucketVersioningResponse getPayload() {
		return super.getPayload();
	}
}

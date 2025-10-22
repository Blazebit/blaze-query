/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.s3.model.GetBucketAclResponse;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsBucketAcl extends AwsWrapper<GetBucketAclResponse> {
	public AwsBucketAcl(String accountId, String region, String resourceId, GetBucketAclResponse payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public GetBucketAclResponse getPayload() {
		return super.getPayload();
	}
}

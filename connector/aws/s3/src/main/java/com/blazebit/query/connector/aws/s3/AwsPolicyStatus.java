/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.s3.model.PolicyStatus;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsPolicyStatus extends AwsWrapper<PolicyStatus> {
	public AwsPolicyStatus(String accountId, String region, String resourceId, PolicyStatus payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public PolicyStatus getPayload() {
		return super.getPayload();
	}
}

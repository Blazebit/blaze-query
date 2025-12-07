/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.s3.model.LifecycleRule;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsLifeCycleRule extends AwsWrapper<LifecycleRule> {
	public AwsLifeCycleRule(String accountId, String region, String resourceId, LifecycleRule payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public LifecycleRule getPayload() {
		return super.getPayload();
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.s3.model.ObjectLockConfiguration;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsObjectLockConfiguration extends AwsWrapper<ObjectLockConfiguration> {
	public AwsObjectLockConfiguration(String accountId, String region, String resourceId, ObjectLockConfiguration payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public ObjectLockConfiguration getPayload() {
		return super.getPayload();
	}
}

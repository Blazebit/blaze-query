/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.s3.model.Grant;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsGrant extends AwsWrapper<Grant> {
	public AwsGrant(String accountId, String region, String resourceId, Grant payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public Grant getPayload() {
		return super.getPayload();
	}
}

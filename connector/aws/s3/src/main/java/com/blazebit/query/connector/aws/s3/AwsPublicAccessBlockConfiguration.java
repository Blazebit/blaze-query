/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.s3.model.PublicAccessBlockConfiguration;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsPublicAccessBlockConfiguration extends AwsWrapper<PublicAccessBlockConfiguration> {
	public AwsPublicAccessBlockConfiguration(String accountId, String region, String resourceId, PublicAccessBlockConfiguration payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public PublicAccessBlockConfiguration getPayload() {
		return super.getPayload();
	}
}

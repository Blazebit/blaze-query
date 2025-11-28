/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.InternetGateway;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2InternetGateway extends AwsWrapper<InternetGateway> {
	public AwsEc2InternetGateway(String accountId, String regionId, String resourceId, InternetGateway payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public InternetGateway getPayload() {
		return super.getPayload();
	}
}

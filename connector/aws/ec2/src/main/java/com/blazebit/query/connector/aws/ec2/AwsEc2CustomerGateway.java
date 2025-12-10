/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.CustomerGateway;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2CustomerGateway extends AwsWrapper<CustomerGateway> {
	public AwsEc2CustomerGateway(String accountId, String regionId, String resourceId, CustomerGateway payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public CustomerGateway getPayload() {
		return super.getPayload();
	}
}

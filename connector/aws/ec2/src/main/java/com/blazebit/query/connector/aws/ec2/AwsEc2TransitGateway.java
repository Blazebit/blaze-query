/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.TransitGateway;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2TransitGateway extends AwsWrapper<TransitGateway> {
	public AwsEc2TransitGateway(String accountId, String regionId, String resourceId, TransitGateway payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public TransitGateway getPayload() {
		return super.getPayload();
	}
}

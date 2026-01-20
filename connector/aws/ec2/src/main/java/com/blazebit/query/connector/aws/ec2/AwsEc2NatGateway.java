/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.NatGateway;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2NatGateway extends AwsWrapper<NatGateway> {
	public AwsEc2NatGateway(String accountId, String regionId, String resourceId, NatGateway payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public NatGateway getPayload() {
		return super.getPayload();
	}
}

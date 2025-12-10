/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.VpnGateway;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2VpnGateway extends AwsWrapper<VpnGateway> {
	public AwsEc2VpnGateway(String accountId, String regionId, String resourceId, VpnGateway payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public VpnGateway getPayload() {
		return super.getPayload();
	}
}

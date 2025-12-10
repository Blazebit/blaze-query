/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.TransitGatewayAttachment;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2TransitGatewayAttachment extends AwsWrapper<TransitGatewayAttachment> {
	public AwsEc2TransitGatewayAttachment(String accountId, String regionId, String resourceId,
									TransitGatewayAttachment payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public TransitGatewayAttachment getPayload() {
		return super.getPayload();
	}
}

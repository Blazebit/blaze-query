/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.VpcEndpoint;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2VpcEndpoint extends AwsWrapper<VpcEndpoint> {
	public AwsEc2VpcEndpoint(String accountId, String regionId, String resourceId, VpcEndpoint payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public VpcEndpoint getPayload() {
		return super.getPayload();
	}
}

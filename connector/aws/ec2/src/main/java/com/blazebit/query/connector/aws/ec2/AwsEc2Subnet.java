/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.Subnet;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2Subnet extends AwsWrapper<Subnet> {
	public AwsEc2Subnet(String accountId, String regionId, String resourceId, Subnet payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public Subnet getPayload() {
		return super.getPayload();
	}
}

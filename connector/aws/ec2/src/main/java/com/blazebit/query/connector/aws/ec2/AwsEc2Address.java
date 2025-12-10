/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.Address;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2Address extends AwsWrapper<Address> {
	public AwsEc2Address(String accountId, String regionId, String resourceId, Address payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public Address getPayload() {
		return super.getPayload();
	}
}

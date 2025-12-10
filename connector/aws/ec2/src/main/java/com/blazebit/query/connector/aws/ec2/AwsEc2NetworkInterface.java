/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.NetworkInterface;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2NetworkInterface extends AwsWrapper<NetworkInterface> {
	public AwsEc2NetworkInterface(String accountId, String regionId, String resourceId, NetworkInterface payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public NetworkInterface getPayload() {
		return super.getPayload();
	}
}

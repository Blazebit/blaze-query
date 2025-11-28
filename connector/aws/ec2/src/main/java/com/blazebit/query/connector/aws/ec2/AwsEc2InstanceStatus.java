/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.InstanceStatus;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2InstanceStatus extends AwsWrapper<InstanceStatus> {
	public AwsEc2InstanceStatus(String accountId, String regionId, String resourceId, InstanceStatus payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public InstanceStatus getPayload() {
		return super.getPayload();
	}
}

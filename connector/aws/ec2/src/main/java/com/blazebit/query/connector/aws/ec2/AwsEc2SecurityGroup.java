/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsEc2SecurityGroup extends AwsWrapper<SecurityGroup> {
	public AwsEc2SecurityGroup(String accountId, String regionId, String resourceId, SecurityGroup payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public SecurityGroup getPayload() {
		return super.getPayload();
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.Instance;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsEc2Instance extends AwsWrapper<Instance> {
	public AwsEc2Instance(String accountId, String regionId, String resourceId, Instance payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public Instance getPayload() {
		return super.getPayload();
	}
}

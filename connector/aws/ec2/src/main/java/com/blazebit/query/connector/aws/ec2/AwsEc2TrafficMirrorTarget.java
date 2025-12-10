/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.TrafficMirrorTarget;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2TrafficMirrorTarget extends AwsWrapper<TrafficMirrorTarget> {
	public AwsEc2TrafficMirrorTarget(String accountId, String regionId, String resourceId, TrafficMirrorTarget payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public TrafficMirrorTarget getPayload() {
		return super.getPayload();
	}
}

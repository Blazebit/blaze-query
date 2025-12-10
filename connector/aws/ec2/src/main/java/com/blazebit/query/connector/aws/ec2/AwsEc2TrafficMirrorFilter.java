/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.TrafficMirrorFilter;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2TrafficMirrorFilter extends AwsWrapper<TrafficMirrorFilter> {
	public AwsEc2TrafficMirrorFilter(String accountId, String regionId, String resourceId, TrafficMirrorFilter payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public TrafficMirrorFilter getPayload() {
		return super.getPayload();
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.TrafficMirrorSession;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsTrafficMirrorSession extends AwsWrapper<TrafficMirrorSession> {
	public AwsTrafficMirrorSession(String accountId, String regionId, String resourceId, TrafficMirrorSession payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public TrafficMirrorSession getPayload() {
		return super.getPayload();
	}
}

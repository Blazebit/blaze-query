/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.SpotFleetRequestConfig;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsSpotFleetRequest extends AwsWrapper<SpotFleetRequestConfig> {
	public AwsSpotFleetRequest(String accountId, String regionId, String resourceId, SpotFleetRequestConfig payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public SpotFleetRequestConfig getPayload() {
		return super.getPayload();
	}
}

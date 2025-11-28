/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.LaunchTemplate;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsLaunchTemplate extends AwsWrapper<LaunchTemplate> {
	public AwsLaunchTemplate(String accountId, String regionId, String resourceId, LaunchTemplate payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public LaunchTemplate getPayload() {
		return super.getPayload();
	}
}

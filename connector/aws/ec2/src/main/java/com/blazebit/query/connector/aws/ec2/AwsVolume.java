/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.Volume;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsVolume extends AwsWrapper<Volume> {
	public AwsVolume(String accountId, String regionId, String resourceId, Volume payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public Volume getPayload() {
		return super.getPayload();
	}
}

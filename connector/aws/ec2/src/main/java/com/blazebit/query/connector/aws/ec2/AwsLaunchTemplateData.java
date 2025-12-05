/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.ResponseLaunchTemplateData;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsLaunchTemplateData extends AwsWrapper<ResponseLaunchTemplateData> {
	public AwsLaunchTemplateData(String accountId, String regionId, String resourceId, ResponseLaunchTemplateData payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public ResponseLaunchTemplateData getPayload() {
		return super.getPayload();
	}
}

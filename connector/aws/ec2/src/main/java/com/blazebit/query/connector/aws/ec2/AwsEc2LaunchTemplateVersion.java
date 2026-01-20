/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.LaunchTemplateVersion;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2LaunchTemplateVersion extends AwsWrapper<LaunchTemplateVersion> {
	public AwsEc2LaunchTemplateVersion(String accountId, String regionId, String resourceId, LaunchTemplateVersion payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public LaunchTemplateVersion getPayload() {
		return super.getPayload();
	}
}

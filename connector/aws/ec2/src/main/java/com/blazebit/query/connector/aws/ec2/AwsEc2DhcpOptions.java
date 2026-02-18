/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.DhcpOptions;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2DhcpOptions extends AwsWrapper<DhcpOptions> {
	public AwsEc2DhcpOptions(String accountId, String regionId, String resourceId, DhcpOptions payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public DhcpOptions getPayload() {
		return super.getPayload();
	}
}

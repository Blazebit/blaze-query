/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.DescribeVpcEndpointsResponse;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsVpcEndpoints extends AwsWrapper<DescribeVpcEndpointsResponse> {
	public AwsVpcEndpoints(String accountId, String regionId, String resourceId, DescribeVpcEndpointsResponse payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public DescribeVpcEndpointsResponse getPayload() {
		return super.getPayload();
	}
}

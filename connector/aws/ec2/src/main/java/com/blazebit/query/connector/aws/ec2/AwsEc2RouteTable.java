/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.RouteTable;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2RouteTable extends AwsWrapper<RouteTable> {
	public AwsEc2RouteTable(String accountId, String regionId, String resourceId, RouteTable payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public RouteTable getPayload() {
		return super.getPayload();
	}
}

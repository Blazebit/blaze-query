/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.TransitGatewayRouteTable;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2TransitGatewayRouteTable extends AwsWrapper<TransitGatewayRouteTable> {
	public AwsEc2TransitGatewayRouteTable(String accountId, String regionId, String resourceId,
									TransitGatewayRouteTable payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public TransitGatewayRouteTable getPayload() {
		return super.getPayload();
	}
}

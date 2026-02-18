/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.VpcPeeringConnection;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2VpcPeeringConnection extends AwsWrapper<VpcPeeringConnection> {
	public AwsEc2VpcPeeringConnection(String accountId, String regionId, String resourceId,
								VpcPeeringConnection payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public VpcPeeringConnection getPayload() {
		return super.getPayload();
	}
}

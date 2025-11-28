/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.VpnConnection;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsVpnConnection extends AwsWrapper<VpnConnection> {
	public AwsVpnConnection(String accountId, String regionId, String resourceId, VpnConnection payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public VpnConnection getPayload() {
		return super.getPayload();
	}
}

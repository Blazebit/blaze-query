/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.NetworkAcl;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsNetworkAcl extends AwsWrapper<NetworkAcl> {
	public AwsNetworkAcl(String accountId, String regionId, String resourceId, NetworkAcl payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public NetworkAcl getPayload() {
		return super.getPayload();
	}
}

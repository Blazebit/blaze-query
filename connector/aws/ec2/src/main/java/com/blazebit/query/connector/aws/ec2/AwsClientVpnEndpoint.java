/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.ClientVpnEndpoint;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsClientVpnEndpoint extends AwsWrapper<ClientVpnEndpoint> {
	public AwsClientVpnEndpoint(String accountId, String regionId, String resourceId, ClientVpnEndpoint payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public ClientVpnEndpoint getPayload() {
		return super.getPayload();
	}
}

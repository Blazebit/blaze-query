/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.Vpc;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsVpc extends AwsWrapper<Vpc> {
	public AwsVpc(String accountId, String regionId, String resourceId, Vpc payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public Vpc getPayload() {
		return super.getPayload();
	}
}

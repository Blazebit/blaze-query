/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.ManagedPrefixList;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2ManagedPrefixList extends AwsWrapper<ManagedPrefixList> {
	public AwsEc2ManagedPrefixList(String accountId, String regionId, String resourceId, ManagedPrefixList payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public ManagedPrefixList getPayload() {
		return super.getPayload();
	}
}

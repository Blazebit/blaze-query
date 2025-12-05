/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.PrefixList;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsPrefixList extends AwsWrapper<PrefixList> {
	public AwsPrefixList(String accountId, String regionId, String resourceId, PrefixList payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public PrefixList getPayload() {
		return super.getPayload();
	}
}

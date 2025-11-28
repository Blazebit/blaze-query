/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.VpcBlockPublicAccessOptions;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2VpcBlockPublicAccessOptions extends AwsWrapper<VpcBlockPublicAccessOptions> {
	public AwsEc2VpcBlockPublicAccessOptions(String accountId, String regionId, String resourceId,
										VpcBlockPublicAccessOptions payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public VpcBlockPublicAccessOptions getPayload() {
		return super.getPayload();
	}
}

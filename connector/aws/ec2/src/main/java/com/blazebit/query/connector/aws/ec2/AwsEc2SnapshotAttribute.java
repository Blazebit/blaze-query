/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.DescribeSnapshotAttributeResponse;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2SnapshotAttribute extends AwsWrapper<DescribeSnapshotAttributeResponse> {
	public AwsEc2SnapshotAttribute(String accountId, String regionId, String resourceId,
								DescribeSnapshotAttributeResponse payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public DescribeSnapshotAttributeResponse getPayload() {
		return super.getPayload();
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.FlowLog;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2FlowLogs extends AwsWrapper<FlowLog> {
	public AwsEc2FlowLogs(String accountId, String regionId, String resourceId, FlowLog payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public FlowLog getPayload() {
		return super.getPayload();
	}
}

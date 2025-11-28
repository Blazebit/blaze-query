/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ec2.model.Snapshot;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsSnapshot extends AwsWrapper<Snapshot> {
	public AwsSnapshot(String accountId, String regionId, String resourceId, Snapshot payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public Snapshot getPayload() {
		return super.getPayload();
	}
}

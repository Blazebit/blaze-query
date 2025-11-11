/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.rds;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.rds.model.DBClusterSnapshot;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsDBClusterSnapshot extends AwsWrapper<DBClusterSnapshot> {
	public AwsDBClusterSnapshot(String arn, DBClusterSnapshot payload) {
		super( arn, payload );
	}

	@Override
	public DBClusterSnapshot getPayload() {
		return super.getPayload();
	}
}

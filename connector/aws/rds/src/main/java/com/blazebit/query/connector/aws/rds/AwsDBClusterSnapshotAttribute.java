/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.rds;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.rds.model.DBClusterSnapshotAttribute;

/**
 * @author Donghwi kim
 * @since 1.0.0
 */
public class AwsDBClusterSnapshotAttribute extends AwsWrapper<DBClusterSnapshotAttribute> {
	public AwsDBClusterSnapshotAttribute(String arn, DBClusterSnapshotAttribute payload) {
		super( arn, payload );
	}

	@Override
	public DBClusterSnapshotAttribute getPayload() {
		return super.getPayload();
	}
}

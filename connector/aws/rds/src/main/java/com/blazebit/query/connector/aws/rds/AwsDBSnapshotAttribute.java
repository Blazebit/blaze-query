/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.rds;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.rds.model.DBSnapshotAttribute;

/**
 * @author Donghwi kim
 * @since 1.0.0
 */
public class AwsDBSnapshotAttribute extends AwsWrapper<DBSnapshotAttribute> {
	public AwsDBSnapshotAttribute(String arn, DBSnapshotAttribute payload) {
		super( arn, payload );
	}

	@Override
	public DBSnapshotAttribute getPayload() {
		return super.getPayload();
	}
}

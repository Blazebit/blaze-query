/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.rds;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.rds.model.DBSnapshotAttributesResult;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsDBSnapshotAttributeResult extends AwsWrapper<DBSnapshotAttributesResult> {
	public AwsDBSnapshotAttributeResult(String arn, DBSnapshotAttributesResult payload) {
		super( arn, payload );
	}

	@Override
	public DBSnapshotAttributesResult getPayload() {
		return super.getPayload();
	}
}

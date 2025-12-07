/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.rds;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.rds.model.DBSnapshot;

/**
 * @author Donghwi KIm
 * @since 1.0.0
 */
public class AwsDBSnapshot extends AwsWrapper<DBSnapshot> {
	public AwsDBSnapshot(String arn, DBSnapshot payload) {
		super( arn, payload );
	}

	@Override
	public DBSnapshot getPayload() {
		return super.getPayload();
	}
}

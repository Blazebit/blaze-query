/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.rds;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.rds.model.DBInstance;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsDBInstance extends AwsWrapper<DBInstance> {
	public AwsDBInstance(String arn, DBInstance payload) {
		super( arn, payload );
	}

	@Override
	public DBInstance getPayload() {
		return super.getPayload();
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.rds;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.rds.model.DBCluster;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsDBCluster extends AwsWrapper<DBCluster> {
	public AwsDBCluster(String arn, DBCluster payload) {
		super( arn, payload );
	}

	@Override
	public DBCluster getPayload() {
		return super.getPayload();
	}
}

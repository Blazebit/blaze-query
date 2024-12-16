/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ecr;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ecr.model.Repository;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsRepository extends AwsWrapper<Repository> {
	public AwsRepository(String arn, Repository payload) {
		super( arn, payload );
	}

	@Override
	public Repository getPayload() {
		return super.getPayload();
	}
}

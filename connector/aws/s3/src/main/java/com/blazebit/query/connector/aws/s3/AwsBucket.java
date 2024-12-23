/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.s3.model.Bucket;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsBucket extends AwsWrapper<Bucket> {
	public AwsBucket(String accountId, String region, String resourceId, Bucket payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public Bucket getPayload() {
		return super.getPayload();
	}
}

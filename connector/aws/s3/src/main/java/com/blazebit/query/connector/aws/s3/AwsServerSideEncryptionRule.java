/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.s3.model.ServerSideEncryptionRule;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsServerSideEncryptionRule extends AwsWrapper<ServerSideEncryptionRule> {
	public AwsServerSideEncryptionRule(String accountId, String region, String resourceId, ServerSideEncryptionRule payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public ServerSideEncryptionRule getPayload() {
		return super.getPayload();
	}
}

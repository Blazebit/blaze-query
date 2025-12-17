/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.kms;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.kms.model.KeyMetadata;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsKmsKey extends AwsWrapper<KeyMetadata> {
	public AwsKmsKey(String accountId, String region, String resourceId, KeyMetadata payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public KeyMetadata getPayload() {
		return super.getPayload();
	}
}

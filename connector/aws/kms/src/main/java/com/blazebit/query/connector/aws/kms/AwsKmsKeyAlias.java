/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.kms;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.kms.model.AliasListEntry;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsKmsKeyAlias extends AwsWrapper<AliasListEntry> {
	public AwsKmsKeyAlias(String accountId, String region, String resourceId, AliasListEntry payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public AliasListEntry getPayload() {
		return super.getPayload();
	}
}

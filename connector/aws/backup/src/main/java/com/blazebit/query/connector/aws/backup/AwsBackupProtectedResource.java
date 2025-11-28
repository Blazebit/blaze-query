/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.backup;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.backup.model.ProtectedResource;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsBackupProtectedResource extends AwsWrapper<ProtectedResource> {

	public AwsBackupProtectedResource(String arn, ProtectedResource payload) {
		super( arn, payload );
	}

	@Override
	public ProtectedResource getPayload() {
		return super.getPayload();
	}
}

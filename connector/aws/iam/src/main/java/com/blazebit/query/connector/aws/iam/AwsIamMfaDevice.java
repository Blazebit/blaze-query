/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.iam.model.MFADevice;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsIamMfaDevice extends AwsWrapper<MFADevice> {
	public AwsIamMfaDevice(String accountId, String resourceId, MFADevice payload) {
		super( accountId, null, resourceId, payload );
	}

	@Override
	public MFADevice getPayload() {
		return super.getPayload();
	}
}

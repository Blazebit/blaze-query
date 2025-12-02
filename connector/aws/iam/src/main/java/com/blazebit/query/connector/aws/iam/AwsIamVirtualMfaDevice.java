/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.iam.model.VirtualMFADevice;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamVirtualMfaDevice extends AwsWrapper<VirtualMFADevice> {
	public AwsIamVirtualMfaDevice(String accountId, String resourceId, VirtualMFADevice payload) {
		super( accountId, null, resourceId, payload );
	}

	@Override
	public VirtualMFADevice getPayload() {
		return super.getPayload();
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.iam.model.PasswordPolicy;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsPasswordPolicy extends AwsWrapper<PasswordPolicy> {
	public AwsPasswordPolicy(String accountId, PasswordPolicy payload) {
		super( accountId, null, null, payload );
	}

	@Override
	public PasswordPolicy getPayload() {
		return super.getPayload();
	}
}

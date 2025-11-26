/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.iam.model.Role;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamRole extends AwsWrapper<Role> {
	public AwsIamRole(String accountId, String resourceId, Role payload) {
		super( accountId, null, resourceId, payload );
	}

	@Override
	public Role getPayload() {
		return super.getPayload();
	}
}

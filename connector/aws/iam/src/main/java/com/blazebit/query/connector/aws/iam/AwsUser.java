/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.iam.model.User;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsUser extends AwsWrapper<User> {
	public AwsUser(String accountId, String resourceId, User payload) {
		super( accountId, null, resourceId, payload );
	}

	@Override
	public User getPayload() {
		return super.getPayload();
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.iam.model.LoginProfile;

/**
 * @author Donghwi KIm
 * @since 1.0.0
 */
public class AwsLoginProfile extends AwsWrapper<LoginProfile> {
	public AwsLoginProfile(String accountId, String resourceId, LoginProfile payload) {
		super( accountId, null, resourceId, payload );
	}

	@Override
	public LoginProfile getPayload() {
		return super.getPayload();
	}
}

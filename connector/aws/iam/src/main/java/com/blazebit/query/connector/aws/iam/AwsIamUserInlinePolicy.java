/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsPolicyWrapper;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamUserInlinePolicy extends AwsPolicyWrapper {

	private final String userName;
	private final String policyName;

	public AwsIamUserInlinePolicy(
			String accountId,
			String userName,
			String policyName,
			String policyDocument) {
		super( accountId, null, null, policyDocument, true );
		this.userName = userName;
		this.policyName = policyName;
	}

	public String getUserName() {
		return userName;
	}

	public String getPolicyName() {
		return policyName;
	}
}

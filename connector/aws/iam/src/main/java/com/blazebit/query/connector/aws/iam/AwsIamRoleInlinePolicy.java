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
public class AwsIamRoleInlinePolicy extends AwsPolicyWrapper {

	private final String roleName;
	private final String policyName;

	public AwsIamRoleInlinePolicy(
			String accountId,
			String roleName,
			String policyName,
			String policyDocument) {
		super( accountId, null, null, policyDocument, true );
		this.roleName = roleName;
		this.policyName = policyName;
	}

	public String getRoleName() {
		return roleName;
	}

	public String getPolicyName() {
		return policyName;
	}
}

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
public class AwsIamGroupInlinePolicy extends AwsPolicyWrapper {

	private final String groupName;
	private final String policyName;

	public AwsIamGroupInlinePolicy(
			String accountId,
			String groupName,
			String policyName,
			String policyDocument) {
		super( accountId, null, null, policyDocument, true );
		this.groupName = groupName;
		this.policyName = policyName;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getPolicyName() {
		return policyName;
	}
}

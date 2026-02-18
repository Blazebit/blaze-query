/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import software.amazon.awssdk.services.iam.model.AttachedPolicy;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public record AwsIamUserAttachedPolicy(
		String accountId,
		String userName,
		String policyName,
		String policyArn
) {
	public static AwsIamUserAttachedPolicy from(String accountId, String userName, AttachedPolicy attachedPolicy) {
		return new AwsIamUserAttachedPolicy(
				accountId,
				userName,
				attachedPolicy.policyName(),
				attachedPolicy.policyArn()
		);
	}
}

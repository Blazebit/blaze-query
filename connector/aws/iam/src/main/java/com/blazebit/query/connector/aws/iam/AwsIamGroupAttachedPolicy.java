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
public record AwsIamGroupAttachedPolicy(
		String accountId,
		String groupName,
		String policyName,
		String policyArn
) {
	public static AwsIamGroupAttachedPolicy from(String accountId, String groupName, AttachedPolicy attachedPolicy) {
		return new AwsIamGroupAttachedPolicy(
				accountId,
				groupName,
				attachedPolicy.policyName(),
				attachedPolicy.policyArn()
		);
	}
}

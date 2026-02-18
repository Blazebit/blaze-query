/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.kms;

import com.blazebit.query.connector.aws.base.AwsPolicyWrapper;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsKmsKeyPolicy extends AwsPolicyWrapper {

	public AwsKmsKeyPolicy(String accountId, String region, String resourceId, String jsonPolicy) {
		super( accountId, region, resourceId, jsonPolicy );
	}
}

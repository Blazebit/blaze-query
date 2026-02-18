/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.blazebit.query.connector.aws.base.AwsPolicyWrapper;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsBucketPolicy extends AwsPolicyWrapper {

	public AwsBucketPolicy(String accountId, String region, String resourceId, String jsonPolicy) {
		super( accountId, region, resourceId, jsonPolicy );
	}
}

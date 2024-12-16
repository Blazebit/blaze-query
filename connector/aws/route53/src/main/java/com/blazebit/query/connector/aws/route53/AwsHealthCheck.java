/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.route53;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.route53.model.HealthCheck;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsHealthCheck extends AwsWrapper<HealthCheck> {
	public AwsHealthCheck(String accountId, String resourceId, HealthCheck payload) {
		super( accountId, null, resourceId, payload );
	}

	@Override
	public HealthCheck getPayload() {
		return super.getPayload();
	}
}

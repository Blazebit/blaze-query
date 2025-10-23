/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.s3.model.LoggingEnabled;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsLoggingEnabled extends AwsWrapper<LoggingEnabled> {
	public AwsLoggingEnabled(String accountId, String region, String resourceId, LoggingEnabled payload) {
		super( accountId, region, resourceId, payload );
	}

	@Override
	public LoggingEnabled getPayload() {
		return super.getPayload();
	}
}

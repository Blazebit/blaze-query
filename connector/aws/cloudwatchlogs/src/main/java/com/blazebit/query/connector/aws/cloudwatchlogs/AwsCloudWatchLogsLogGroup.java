/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.cloudwatchlogs;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogGroup;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsCloudWatchLogsLogGroup extends AwsWrapper<LogGroup> {
	public AwsCloudWatchLogsLogGroup(String arn, LogGroup payload) {
		super( arn, payload );
	}

	@Override
	public LogGroup getPayload() {
		return super.getPayload();
	}
}

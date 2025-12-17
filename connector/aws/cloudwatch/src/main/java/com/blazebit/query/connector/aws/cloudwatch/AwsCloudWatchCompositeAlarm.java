/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.cloudwatch;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.cloudwatch.model.CompositeAlarm;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsCloudWatchCompositeAlarm extends AwsWrapper<CompositeAlarm> {
	public AwsCloudWatchCompositeAlarm(String arn, CompositeAlarm payload) {
		super( arn, payload );
	}

	@Override
	public CompositeAlarm getPayload() {
		return super.getPayload();
	}
}

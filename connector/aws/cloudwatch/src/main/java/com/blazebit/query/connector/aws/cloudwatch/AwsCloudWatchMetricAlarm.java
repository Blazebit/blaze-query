/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.cloudwatch;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.cloudwatch.model.MetricAlarm;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsCloudWatchMetricAlarm extends AwsWrapper<MetricAlarm> {
	public AwsCloudWatchMetricAlarm(String arn, MetricAlarm payload) {
		super( arn, payload );
	}

	@Override
	public MetricAlarm getPayload() {
		return super.getPayload();
	}
}

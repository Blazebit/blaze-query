/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.cloudwatchlogs;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.cloudwatchlogs.model.MetricFilter;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsCloudWatchLogsMetricFilter extends AwsWrapper<MetricFilter> {
	public AwsCloudWatchLogsMetricFilter(String accountId, String regionId, String resourceId, MetricFilter payload) {
		super( accountId, regionId, resourceId, payload );
	}

	@Override
	public MetricFilter getPayload() {
		return super.getPayload();
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ecs;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ecs.model.TaskSet;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEcsTaskSet extends AwsWrapper<TaskSet> {
	public AwsEcsTaskSet(String arn, TaskSet payload) {
		super( arn, payload );
	}

	@Override
	public TaskSet getPayload() {
		return super.getPayload();
	}
}

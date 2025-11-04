/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ecs;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ecs.model.TaskDefinition;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsTaskDefinition extends AwsWrapper<TaskDefinition> {
	public AwsTaskDefinition(String arn, TaskDefinition payload) {
		super( arn, payload );
	}

	@Override
	public TaskDefinition getPayload() {
		return super.getPayload();
	}
}

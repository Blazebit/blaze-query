/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ecs;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ecs.model.DescribeTaskDefinitionResponse;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEcsTaskDefinition extends AwsWrapper<DescribeTaskDefinitionResponse> {
	public AwsEcsTaskDefinition(String arn, DescribeTaskDefinitionResponse payload) {
		super( arn, payload );
	}

	@Override
	public DescribeTaskDefinitionResponse getPayload() {
		return super.getPayload();
	}
}

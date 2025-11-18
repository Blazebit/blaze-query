/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ecs;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.ecs.model.Service;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEcsService extends AwsWrapper<Service> {
	public AwsEcsService(String arn, Service payload) {
		super( arn, payload );
	}

	@Override
	public Service getPayload() {
		return super.getPayload();
	}
}

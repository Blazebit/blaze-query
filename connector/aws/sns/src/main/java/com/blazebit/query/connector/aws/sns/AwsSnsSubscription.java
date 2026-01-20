/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.sns;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.sns.model.Subscription;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsSnsSubscription extends AwsWrapper<Subscription> {
	public AwsSnsSubscription(String arn, Subscription payload) {
		super( arn, payload );
	}

	@Override
	public Subscription getPayload() {
		return super.getPayload();
	}
}

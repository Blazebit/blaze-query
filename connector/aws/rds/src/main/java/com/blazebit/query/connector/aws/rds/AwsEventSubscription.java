/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.rds;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.rds.model.EventSubscription;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEventSubscription extends AwsWrapper<EventSubscription> {
	public AwsEventSubscription(String arn, EventSubscription payload) {
		super( arn, payload );
	}

	@Override
	public EventSubscription getPayload() {
		return super.getPayload();
	}
}

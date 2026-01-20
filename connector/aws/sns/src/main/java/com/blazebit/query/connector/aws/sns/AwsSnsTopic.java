/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.sns;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.sns.model.Topic;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsSnsTopic extends AwsWrapper<Topic> {
	public AwsSnsTopic(String arn, Topic payload) {
		super( arn, payload );
	}

	@Override
	public Topic getPayload() {
		return super.getPayload();
	}
}

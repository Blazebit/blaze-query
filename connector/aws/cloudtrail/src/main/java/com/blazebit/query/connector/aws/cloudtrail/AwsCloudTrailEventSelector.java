/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.cloudtrail;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.cloudtrail.model.GetEventSelectorsResponse;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsCloudTrailEventSelector extends AwsWrapper<GetEventSelectorsResponse> {
	public AwsCloudTrailEventSelector(String arn, GetEventSelectorsResponse payload) {
		super( arn, payload );
	}

	@Override
	public GetEventSelectorsResponse getPayload() {
		return super.getPayload();
	}
}

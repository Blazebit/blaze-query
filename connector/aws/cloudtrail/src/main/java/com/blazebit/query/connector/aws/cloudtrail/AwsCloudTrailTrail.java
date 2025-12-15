/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.cloudtrail;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.cloudtrail.model.Trail;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsCloudTrailTrail extends AwsWrapper<Trail> {
	public AwsCloudTrailTrail(String arn, Trail payload) {
		super( arn, payload );
	}

	@Override
	public Trail getPayload() {
		return super.getPayload();
	}
}

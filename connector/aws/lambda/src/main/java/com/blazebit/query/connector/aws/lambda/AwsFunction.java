/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.lambda;

import com.blazebit.query.connector.aws.base.AwsWrapper;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsFunction extends AwsWrapper<FunctionConfiguration> {
	public AwsFunction(String arn, FunctionConfiguration payload) {
		super( arn, payload );
	}

	@Override
	public FunctionConfiguration getPayload() {
		return super.getPayload();
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.lambda;

import java.util.Map;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;

/**
 * The schema provider for the AWS Lambda connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AwsLambdaSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public AwsLambdaSchemaProvider() {
	}

	@Override
	public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Map.<Class<?>, DataFetcher<?>>of(
				FunctionConfiguration.class, FunctionConfigurationDataFetcher.INSTANCE
		);
	}
}

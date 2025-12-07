/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ecs;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the AWS ECS connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AwsECSSchemaProvider implements QuerySchemaProvider {
	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				AwsEcsClusterDataFetcher.INSTANCE,
				AwsEcsServiceDataFetcher.INSTANCE,
				AwsEcsTaskDefinitionDataFetcher.INSTANCE,
				AwsEcsContainerDefinitionDataFetcher.INSTANCE,
				AwsEcsTaskSetDataFetcher.INSTANCE
		);
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ecr;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the AWS ECR connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AwsECRSchemaProvider implements QuerySchemaProvider {
	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				RepositoryDataFetcher.INSTANCE
		);
	}
}

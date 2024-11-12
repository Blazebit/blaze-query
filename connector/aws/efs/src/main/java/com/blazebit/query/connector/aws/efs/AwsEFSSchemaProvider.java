/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.efs;

import java.util.Map;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;
import software.amazon.awssdk.services.efs.model.FileSystemDescription;

/**
 * The schema provider for the AWS EFS connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AwsEFSSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public AwsEFSSchemaProvider() {
	}

	@Override
	public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Map.<Class<?>, DataFetcher<?>>of(
				FileSystemDescription.class, FileSystemDataFetcher.INSTANCE
		);
	}
}

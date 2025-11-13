/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the AWS S3 connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AwsS3SchemaProvider implements QuerySchemaProvider {
	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				BucketDataFetcher.INSTANCE,
				BucketAclFetcher.INSTANCE,
				BucketVersioningFetcher.INSTANCE,
				BucketPolicyFetcher.INSTANCE,
				LifecycleRuleFetcher.INSTANCE,
				LoggingEnabledFetcher.INSTANCE,
				ObjectLockConfigurationFetcher.INSTANCE,
				PolicyStatusFetcher.INSTANCE,
				PublicAccessBlockConfigurationFetcher.INSTANCE,
				ServerSideEncryptionRuleFetcher.INSTANCE
		);
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the AWS EC2 connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AwsEc2SchemaProvider implements QuerySchemaProvider {
	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				InstanceDataFetcher.INSTANCE,
				VpcDataFetcher.INSTANCE,
				SecurityGroupDataFetcher.INSTANCE,
				VolumeDataFetcher.INSTANCE,
				NetworkAclDataFetcher.INSTANCE
		);
	}
}

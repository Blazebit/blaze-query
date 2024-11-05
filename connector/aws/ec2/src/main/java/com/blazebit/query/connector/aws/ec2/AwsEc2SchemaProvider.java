/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import java.util.Map;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.NetworkAcl;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Volume;
import software.amazon.awssdk.services.ec2.model.Vpc;

/**
 * The schema provider for the AWS EC2 connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AwsEc2SchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public AwsEc2SchemaProvider() {
	}

	@Override
	public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Map.<Class<?>, DataFetcher<?>>of(
				Instance.class, InstanceDataFetcher.INSTANCE,
				Vpc.class, VpcDataFetcher.INSTANCE,
				SecurityGroup.class, SecurityGroupDataFetcher.INSTANCE,
				Volume.class, VolumeDataFetcher.INSTANCE,
				NetworkAcl.class, NetworkAclDataFetcher.INSTANCE
		);
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import java.util.Map;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;
import software.amazon.awssdk.services.iam.model.PasswordPolicy;
import software.amazon.awssdk.services.iam.model.MFADevice;
import software.amazon.awssdk.services.iam.model.User;

/**
 * The schema provider for the AWS IAM connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AwsIAMSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public AwsIAMSchemaProvider() {
	}

	@Override
	public Map<Class<?>, ? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Map.<Class<?>, DataFetcher<?>>of(
				User.class, UserDataFetcher.INSTANCE,
				PasswordPolicy.class, PasswordPolicyDataFetcher.INSTANCE,
				MFADevice.class, MFADeviceDataFetcher.INSTANCE,
				AccountSummary.class, AccountSummaryDataFetcher.INSTANCE
		);
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.base;

import com.blazebit.query.spi.DataFetcherConfig;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;

/**
 * Configuration properties for the AWS {@link com.blazebit.query.spi.DataFetcher} instances.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class AwsConnectorConfig {

	/**
	 * Specifies the {@link Account} to use for querying data.
	 */
	public static final DataFetcherConfig<Account> ACCOUNT = DataFetcherConfig.forPropertyName( "awsAccount" );
	/**
	 * Specifies the {@link SdkHttpClient} to use for querying data.
	 */
	public static final DataFetcherConfig<SdkHttpClient> HTTP_CLIENT = DataFetcherConfig.forPropertyName(
			"awsHttpClient" );

	private AwsConnectorConfig() {
	}

	/**
	 * Account configuration.
	 *
	 * @author Christian Beikov
	 * @since 1.0.0
	 */
	public static final class Account {
		private final Region region;
		private final AwsCredentialsProvider credentialsProvider;

		/**
		 * Create a new account.
		 *
		 * @param region The region to use for the account.
		 * @param credentialsProvider The credentials to use.
		 */
		public Account(Region region, AwsCredentialsProvider credentialsProvider) {
			this.region = region;
			this.credentialsProvider = credentialsProvider;
		}

		/**
		 * Returns the AWS region for the account.
		 *
		 * @return the AWS region for the account
		 */
		public Region getRegion() {
			return region;
		}

		/**
		 * Returns the AWS credentials provider for the account.
		 *
		 * @return the AWS credentials provider for the account
		 */
		public AwsCredentialsProvider getCredentialsProvider() {
			return credentialsProvider;
		}
	}
}

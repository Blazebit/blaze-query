/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    public static final DataFetcherConfig<SdkHttpClient> HTTP_CLIENT = DataFetcherConfig.forPropertyName( "awsHttpClient" );

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

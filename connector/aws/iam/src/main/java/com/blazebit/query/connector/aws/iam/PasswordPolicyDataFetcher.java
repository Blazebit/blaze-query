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

package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.IamClientBuilder;
import software.amazon.awssdk.services.iam.model.NoSuchEntityException;
import software.amazon.awssdk.services.iam.model.PasswordPolicy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martijn Sprengers
 * @since 1.0.0
 */
public class PasswordPolicyDataFetcher implements DataFetcher<PasswordPolicy>, Serializable {

    public static final PasswordPolicyDataFetcher INSTANCE = new PasswordPolicyDataFetcher();

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.componentMethodConvention(PasswordPolicy.class, AwsConventionContext.INSTANCE);
    }

    @Override
    public List<PasswordPolicy> fetch(DataFetchContext context) {
        try {
            List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll(context);
            SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find(context);
            List<PasswordPolicy> list = new ArrayList<>();
            for (AwsConnectorConfig.Account account : accounts) {
                IamClientBuilder ec2ClientBuilder = IamClient.builder()
                        .region(account.getRegion())
                        .credentialsProvider(account.getCredentialsProvider());
                if (sdkHttpClient != null) {
                    ec2ClientBuilder.httpClient(sdkHttpClient);
                }
                try (IamClient client = ec2ClientBuilder.build()) {
                    list.add(client.getAccountPasswordPolicy().passwordPolicy());
                } catch (NoSuchEntityException e) {
                    // The AWS SDK throws a NoSuchEntity exception if the password policy is default
                    return list;
                }
            }
            return list;
        } catch (RuntimeException e) {
            throw new DataFetcherException("Could not fetch password policy", e);
        }
    }
}

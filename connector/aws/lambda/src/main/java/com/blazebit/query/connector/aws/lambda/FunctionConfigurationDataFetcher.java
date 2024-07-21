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

package com.blazebit.query.connector.aws.lambda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.LambdaClientBuilder;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class FunctionConfigurationDataFetcher implements DataFetcher<FunctionConfiguration>, Serializable {

    public static final FunctionConfigurationDataFetcher INSTANCE = new FunctionConfigurationDataFetcher();

    private FunctionConfigurationDataFetcher() {
    }

    @Override
    public List<FunctionConfiguration> fetch(DataFetchContext context) {
        try {
            List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
            SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
            List<FunctionConfiguration> list = new ArrayList<>();
            for ( AwsConnectorConfig.Account account : accounts) {
                LambdaClientBuilder ec2ClientBuilder = LambdaClient.builder()
                        .region( account.getRegion() )
                        .credentialsProvider( account.getCredentialsProvider() );
                if ( sdkHttpClient != null ) {
                    ec2ClientBuilder.httpClient( sdkHttpClient );
                }
                try (LambdaClient client = ec2ClientBuilder.build()) {
                    list.addAll( client.listFunctions().functions() );
                }
            }
            return list;
        } catch (RuntimeException e) {
            throw new DataFetcherException("Could not fetch function list", e);
        }
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.componentMethodConvention(FunctionConfiguration.class, AwsConventionContext.INSTANCE);
    }
}

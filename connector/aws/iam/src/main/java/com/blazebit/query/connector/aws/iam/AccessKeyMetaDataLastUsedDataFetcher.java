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
import software.amazon.awssdk.services.iam.model.AccessKeyMetadata;
import software.amazon.awssdk.services.iam.model.ListAccessKeysResponse;
import software.amazon.awssdk.services.iam.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martijn Sprengers
 * @since 1.0.0
 */
public class AccessKeyMetaDataLastUsedDataFetcher implements DataFetcher<AccessKeyMetaDataLastUsed>, Serializable {

    public static final AccessKeyMetaDataLastUsedDataFetcher INSTANCE = new AccessKeyMetaDataLastUsedDataFetcher();

    private AccessKeyMetaDataLastUsedDataFetcher() {
    }

    @Override
    public DataFormat getDataFormat() {
        return DataFormats.componentMethodConvention(AccessKeyMetaDataLastUsed.class, AwsConventionContext.INSTANCE);
    }

    @Override
    public List<AccessKeyMetaDataLastUsed> fetch(DataFetchContext context) {
        try {
            List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
            SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
            List<AccessKeyMetaDataLastUsed> list = new ArrayList<>();
            for ( AwsConnectorConfig.Account account : accounts) {
                IamClientBuilder iamClientBuilder = IamClient.builder()
                        .region( account.getRegion() )
                        .credentialsProvider( account.getCredentialsProvider() );
                if ( sdkHttpClient != null ) {
                    iamClientBuilder.httpClient( sdkHttpClient );
                }
                try (IamClient client = iamClientBuilder.build()) {
                    for (User user : client.listUsers().users()) {
                        String marker = null;
                        boolean isTruncated;

                        // Handle pagination
                        do {
                            String finalMarker = marker;
                            ListAccessKeysResponse response = client.listAccessKeys(
                                    builder -> builder.userName(user.userName()).marker(finalMarker)
                            );

                            for (AccessKeyMetadata accessKeyMetadata : response.accessKeyMetadata()) {
                                list.add(new AccessKeyMetaDataLastUsed(
                                        accessKeyMetadata,
                                        client.getAccessKeyLastUsed(builder -> builder.accessKeyId(accessKeyMetadata.accessKeyId())).accessKeyLastUsed()
                                ));
                            }

                            // Update marker for the next request
                            marker = response.marker();
                            isTruncated = response.isTruncated();
                        } while (isTruncated);
                    }
                }
            }
            return list;
        } catch (RuntimeException e) {
            throw new DataFetcherException("Could not fetch access key list", e);
        }
    }
}

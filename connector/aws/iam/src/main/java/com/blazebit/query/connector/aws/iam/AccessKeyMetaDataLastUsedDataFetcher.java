/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
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
import software.amazon.awssdk.services.iam.model.ListAccessKeysRequest;
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
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
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
							ListAccessKeysRequest request = ListAccessKeysRequest.builder()
									.userName( user.userName() )
									.marker( marker )
									.build();
							ListAccessKeysResponse response = client.listAccessKeys( request );

							for (AccessKeyMetadata accessKeyMetadata : response.accessKeyMetadata()) {
								list.add(new AccessKeyMetaDataLastUsed(
										account.getAccountId(),
										user.userName(),
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

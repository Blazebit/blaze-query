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
import software.amazon.awssdk.services.iam.model.GetUserPolicyResponse;
import software.amazon.awssdk.services.iam.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamUserInlinePolicyDataFetcher implements DataFetcher<AwsIamUserInlinePolicy>, Serializable {

	public static final AwsIamUserInlinePolicyDataFetcher INSTANCE = new AwsIamUserInlinePolicyDataFetcher();

	private AwsIamUserInlinePolicyDataFetcher() {
	}

	@Override
	public List<AwsIamUserInlinePolicy> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsIamUserInlinePolicy> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					for ( User user : client.listUsersPaginator().users() ) {
						for ( String policyName : client.listUserPoliciesPaginator(
								builder -> builder.userName( user.userName() )
						).policyNames() ) {
							GetUserPolicyResponse policyResponse = client.getUserPolicy(
									builder -> builder.userName( user.userName() ).policyName( policyName )
							);
							list.add( AwsIamUserInlinePolicy.fromJson(
									account.getAccountId(),
									user.userName(),
									policyName,
									policyResponse.policyDocument()
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch user inline policies", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsIamUserInlinePolicy.class, AwsConventionContext.INSTANCE );
	}
}

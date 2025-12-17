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
import software.amazon.awssdk.services.iam.model.GetGroupPolicyResponse;
import software.amazon.awssdk.services.iam.model.Group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamGroupInlinePolicyDataFetcher implements DataFetcher<AwsIamGroupInlinePolicy>, Serializable {

	public static final AwsIamGroupInlinePolicyDataFetcher INSTANCE = new AwsIamGroupInlinePolicyDataFetcher();

	private AwsIamGroupInlinePolicyDataFetcher() {
	}

	@Override
	public List<AwsIamGroupInlinePolicy> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsIamGroupInlinePolicy> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					for ( Group group : client.listGroupsPaginator().groups() ) {
						for ( String policyName : client.listGroupPoliciesPaginator(
								builder -> builder.groupName( group.groupName() )
						).policyNames() ) {
							GetGroupPolicyResponse policyResponse = client.getGroupPolicy(
									builder -> builder.groupName( group.groupName() ).policyName( policyName )
							);
							list.add( new AwsIamGroupInlinePolicy(
									account.getAccountId(),
									group.groupName(),
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
			throw new DataFetcherException( "Could not fetch group inline policies", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsIamGroupInlinePolicy.class, AwsConventionContext.INSTANCE );
	}
}

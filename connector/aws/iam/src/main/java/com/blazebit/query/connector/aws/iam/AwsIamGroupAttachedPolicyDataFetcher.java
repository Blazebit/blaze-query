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
import software.amazon.awssdk.services.iam.model.AttachedPolicy;
import software.amazon.awssdk.services.iam.model.Group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamGroupAttachedPolicyDataFetcher implements DataFetcher<AwsIamGroupAttachedPolicy>, Serializable {

	public static final AwsIamGroupAttachedPolicyDataFetcher INSTANCE = new AwsIamGroupAttachedPolicyDataFetcher();

	private AwsIamGroupAttachedPolicyDataFetcher() {
	}

	@Override
	public List<AwsIamGroupAttachedPolicy> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsIamGroupAttachedPolicy> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					// Get all groups
					for ( Group group : client.listGroupsPaginator().groups() ) {
						// For each group, list attached managed policies
						for ( AttachedPolicy attachedPolicy : client.listAttachedGroupPoliciesPaginator(
								builder -> builder.groupName( group.groupName() )
						).attachedPolicies() ) {
							list.add( AwsIamGroupAttachedPolicy.from(
									account.getAccountId(),
									group.groupName(),
									attachedPolicy
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch group attached policies", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsIamGroupAttachedPolicy.class, AwsConventionContext.INSTANCE );
	}
}

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
import software.amazon.awssdk.services.iam.model.Role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamRoleAttachedPolicyDataFetcher implements DataFetcher<AwsIamRoleAttachedPolicy>, Serializable {

	public static final AwsIamRoleAttachedPolicyDataFetcher INSTANCE = new AwsIamRoleAttachedPolicyDataFetcher();

	private AwsIamRoleAttachedPolicyDataFetcher() {
	}

	@Override
	public List<AwsIamRoleAttachedPolicy> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsIamRoleAttachedPolicy> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					// Get all roles
					for ( Role role : client.listRolesPaginator().roles() ) {
						// For each role, list attached managed policies
						for ( AttachedPolicy attachedPolicy : client.listAttachedRolePoliciesPaginator(
								builder -> builder.roleName( role.roleName() )
						).attachedPolicies() ) {
							list.add( AwsIamRoleAttachedPolicy.from(
									account.getAccountId(),
									role.roleName(),
									attachedPolicy
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch role attached policies", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsIamRoleAttachedPolicy.class, AwsConventionContext.INSTANCE );
	}
}

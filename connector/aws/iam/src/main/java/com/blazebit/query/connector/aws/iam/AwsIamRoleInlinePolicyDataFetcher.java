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
import software.amazon.awssdk.services.iam.model.GetRolePolicyResponse;
import software.amazon.awssdk.services.iam.model.Role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamRoleInlinePolicyDataFetcher implements DataFetcher<AwsIamRoleInlinePolicy>, Serializable {

	public static final AwsIamRoleInlinePolicyDataFetcher INSTANCE = new AwsIamRoleInlinePolicyDataFetcher();

	private AwsIamRoleInlinePolicyDataFetcher() {
	}

	@Override
	public List<AwsIamRoleInlinePolicy> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsIamRoleInlinePolicy> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					for ( Role role : client.listRolesPaginator().roles() ) {
						for ( String policyName : client.listRolePoliciesPaginator(
								builder -> builder.roleName( role.roleName() )
						).policyNames() ) {
							GetRolePolicyResponse policyResponse = client.getRolePolicy(
									builder -> builder.roleName( role.roleName() ).policyName( policyName )
							);
							list.add( new AwsIamRoleInlinePolicy(
									account.getAccountId(),
									role.roleName(),
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
			throw new DataFetcherException( "Could not fetch role inline policies", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsIamRoleInlinePolicy.class, AwsConventionContext.INSTANCE );
	}
}

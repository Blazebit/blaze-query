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
import software.amazon.awssdk.services.iam.model.GetPolicyVersionRequest;
import software.amazon.awssdk.services.iam.model.ListPoliciesRequest;
import software.amazon.awssdk.services.iam.model.ListPolicyVersionsRequest;
import software.amazon.awssdk.services.iam.model.Policy;
import software.amazon.awssdk.services.iam.model.PolicyScopeType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsIamPolicyDataFetcher implements DataFetcher<AwsIamPolicyVersion>, Serializable {

	public static final AwsIamPolicyDataFetcher INSTANCE = new AwsIamPolicyDataFetcher();

	private AwsIamPolicyDataFetcher() {
	}

	@Override
	public List<AwsIamPolicyVersion> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsIamPolicyVersion> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					// Fetch only customer managed policies (not AWS managed)
					var listPoliciesRequest = ListPoliciesRequest.builder()
							.scope( PolicyScopeType.LOCAL )
							.build();

					for ( Policy policy : client.listPoliciesPaginator( listPoliciesRequest ).policies() ) {
						var listPolicyVersionsRequest = ListPolicyVersionsRequest.builder()
								.policyArn( policy.arn() )
								.build();

						// Collect every version for the local policy
						for ( var policyVersionMetadata : client.listPolicyVersionsPaginator( listPolicyVersionsRequest ).versions() ) {
							var getPolicyVersionRequest = GetPolicyVersionRequest.builder()
									.policyArn( policy.arn() )
									.versionId( policyVersionMetadata.versionId() )
									.build();

							var policyVersion = client.getPolicyVersion( getPolicyVersionRequest );

							list.add( new AwsIamPolicyVersion(
									account.getAccountId(),
									policy.arn(),
									policyVersionMetadata.versionId(),
									policyVersionMetadata.isDefaultVersion(),
									policyVersionMetadata.createDate(),
									policyVersion.policyVersion().document()
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch IAM policy list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsIamPolicyVersion.class, AwsConventionContext.INSTANCE );
	}
}

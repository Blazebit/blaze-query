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
import software.amazon.awssdk.services.iam.model.NoSuchEntityException;
import software.amazon.awssdk.services.iam.model.PasswordPolicy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martijn Sprengers
 * @since 1.0.0
 */
public class PasswordPolicyDataFetcher implements DataFetcher<AwsPasswordPolicy>, Serializable {

	public static final PasswordPolicyDataFetcher INSTANCE = new PasswordPolicyDataFetcher();

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsPasswordPolicy.class, AwsConventionContext.INSTANCE );
	}

	@Override
	public List<AwsPasswordPolicy> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsPasswordPolicy> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					PasswordPolicy passwordPolicy = client.getAccountPasswordPolicy().passwordPolicy();
					list.add( new AwsPasswordPolicy( account.getAccountId(), passwordPolicy ) );
				}
				catch (NoSuchEntityException e) {
					// The AWS SDK throws a NoSuchEntity exception if the password policy is default
					return list;
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch password policy", e );
		}
	}
}

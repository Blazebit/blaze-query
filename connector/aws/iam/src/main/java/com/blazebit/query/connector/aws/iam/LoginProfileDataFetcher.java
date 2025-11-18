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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.IamClientBuilder;
import software.amazon.awssdk.services.iam.model.GetLoginProfileRequest;
import software.amazon.awssdk.services.iam.model.GetLoginProfileResponse;
import software.amazon.awssdk.services.iam.model.IamException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Donghwi KIm
 * @since 1.0.0
 */
public class LoginProfileDataFetcher implements DataFetcher<AwsIamLoginProfile>, Serializable {

	public static final LoginProfileDataFetcher INSTANCE = new LoginProfileDataFetcher();
	private static final Logger log = LoggerFactory.getLogger( LoginProfileDataFetcher.class );

	private LoginProfileDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsIamLoginProfile.class, AwsConventionContext.INSTANCE );
	}

	@Override
	public List<AwsIamLoginProfile> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<? extends AwsUser> users = context.getSession().getOrFetch( AwsUser.class );
			List<AwsIamLoginProfile> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					for ( AwsUser user : users ) {
						if ( user.getAccountId().equals( account.getAccountId() ) ) {
							try {
								GetLoginProfileRequest request = GetLoginProfileRequest.builder()
										.userName( user.getPayload().userName() )
										.build();
								GetLoginProfileResponse response = client.getLoginProfile( request );
								list.add( new AwsIamLoginProfile(
										account.getAccountId(),
										null,
										response.loginProfile()
								) );
							}
							catch (IamException e) {
									// Ignore NoSuchEntity - not all users have login profiles for console access
								if ( Objects.equals( e.awsErrorDetails().errorCode(),
										"NoSuchEntity" ) ) {
									continue;
								}
								throw e;
							}
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch login profile list", e );
		}
	}
}

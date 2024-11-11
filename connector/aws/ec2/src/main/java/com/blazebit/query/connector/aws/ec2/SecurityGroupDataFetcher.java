/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.Ec2ClientBuilder;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class SecurityGroupDataFetcher implements DataFetcher<SecurityGroup>, Serializable {

	public static final SecurityGroupDataFetcher INSTANCE = new SecurityGroupDataFetcher();

	private SecurityGroupDataFetcher() {
	}

	@Override
	public List<SecurityGroup> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<SecurityGroup> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				Ec2ClientBuilder ec2ClientBuilder = Ec2Client.builder()
						.region( account.getRegion() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					ec2ClientBuilder.httpClient( sdkHttpClient );
				}
				try (Ec2Client client = ec2ClientBuilder.build()) {
					list.addAll( client.describeSecurityGroups().securityGroups() );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch security group list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( SecurityGroup.class, AwsConventionContext.INSTANCE );
	}
}

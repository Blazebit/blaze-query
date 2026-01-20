/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.Ec2ClientBuilder;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AwsEc2SecurityGroupDataFetcher implements DataFetcher<AwsEc2SecurityGroup>, Serializable {

	public static final AwsEc2SecurityGroupDataFetcher INSTANCE = new AwsEc2SecurityGroupDataFetcher();

	private AwsEc2SecurityGroupDataFetcher() {
	}

	@Override
	public List<AwsEc2SecurityGroup> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsEc2SecurityGroup> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					Ec2ClientBuilder ec2ClientBuilder = Ec2Client.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ec2ClientBuilder.httpClient( sdkHttpClient );
					}
					try (Ec2Client client = ec2ClientBuilder.build()) {
						for ( SecurityGroup securityGroup : client.describeSecurityGroups().securityGroups() ) {
							list.add( new AwsEc2SecurityGroup(
									securityGroup.ownerId(),
									region.id(),
									securityGroup.groupId(),
									securityGroup
							) );
						}
					}
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
		return DataFormats.componentMethodConvention( AwsEc2SecurityGroup.class, AwsConventionContext.INSTANCE );
	}
}

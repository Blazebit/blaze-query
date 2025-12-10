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
import software.amazon.awssdk.services.ec2.model.VpcBlockPublicAccessOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2VpcBlockPublicAccessOptionsDataFetcher
		implements DataFetcher<AwsEc2VpcBlockPublicAccessOptions>, Serializable {

	public static final AwsEc2VpcBlockPublicAccessOptionsDataFetcher INSTANCE = new AwsEc2VpcBlockPublicAccessOptionsDataFetcher();

	private AwsEc2VpcBlockPublicAccessOptionsDataFetcher() {
	}

	@Override
	public List<AwsEc2VpcBlockPublicAccessOptions> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsEc2VpcBlockPublicAccessOptions> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					Ec2ClientBuilder ec2ClientBuilder = Ec2Client.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ec2ClientBuilder.httpClient( sdkHttpClient );
					}
					try (Ec2Client client = ec2ClientBuilder.build()) {
						VpcBlockPublicAccessOptions options = client.describeVpcBlockPublicAccessOptions( r -> {
						} ).vpcBlockPublicAccessOptions();
						if ( options != null ) {
							list.add( new AwsEc2VpcBlockPublicAccessOptions(
									account.getAccountId(),
									region.id(),
									null,
									options
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch VPC block public access options list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsEc2VpcBlockPublicAccessOptions.class,
				AwsConventionContext.INSTANCE );
	}
}

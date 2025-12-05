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
import software.amazon.awssdk.services.ec2.model.DescribeVpcEndpointsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVpcEndpointsResponse;
import software.amazon.awssdk.services.ec2.model.VpcEndpoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class VpcEndpointsDataFetcher implements DataFetcher<AwsVpcEndpoints>, Serializable {

	public static final VpcEndpointsDataFetcher INSTANCE = new VpcEndpointsDataFetcher();

	private VpcEndpointsDataFetcher() {
	}

	@Override
	public List<AwsVpcEndpoints> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsVpcEndpoints> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					Ec2ClientBuilder ec2ClientBuilder = Ec2Client.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ec2ClientBuilder.httpClient( sdkHttpClient );
					}
					try (Ec2Client client = ec2ClientBuilder.build()) {
						List<VpcEndpoint> vpcEndpoints = new ArrayList<>();
						DescribeVpcEndpointsResponse firstResponse = null;
						String nextToken = null;
						do {
							DescribeVpcEndpointsResponse response = client.describeVpcEndpoints(
									DescribeVpcEndpointsRequest.builder()
											.nextToken( nextToken )
											.build()
							);
							if ( firstResponse == null ) {
								firstResponse = response;
							}
							vpcEndpoints.addAll( response.vpcEndpoints() );
							nextToken = response.nextToken();
						}
						while ( nextToken != null );

						if ( firstResponse != null ) {
							DescribeVpcEndpointsResponse aggregatedResponse = firstResponse.toBuilder()
									.vpcEndpoints( vpcEndpoints )
									.nextToken( null )
									.build();
							list.add( new AwsVpcEndpoints(
									account.getAccountId(),
									region.id(),
									region.id() + ":vpc-endpoints",
									aggregatedResponse
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch vpc endpoints list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsVpcEndpoints.class, AwsConventionContext.INSTANCE );
	}
}

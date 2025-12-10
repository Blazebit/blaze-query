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
import software.amazon.awssdk.services.ec2.model.SpotFleetLaunchSpecification;
import software.amazon.awssdk.services.ec2.model.SpotFleetRequestConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2SpotFleetRequestLaunchSpecificationDataFetcher implements DataFetcher<AwsEc2SpotFleetRequestLaunchSpecification>, Serializable {

	public static final AwsEc2SpotFleetRequestLaunchSpecificationDataFetcher INSTANCE = new AwsEc2SpotFleetRequestLaunchSpecificationDataFetcher();

	private AwsEc2SpotFleetRequestLaunchSpecificationDataFetcher() {
	}

	@Override
	public List<AwsEc2SpotFleetRequestLaunchSpecification> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsEc2SpotFleetRequestLaunchSpecification> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					Ec2ClientBuilder ec2ClientBuilder = Ec2Client.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ec2ClientBuilder.httpClient( sdkHttpClient );
					}
					try (Ec2Client client = ec2ClientBuilder.build()) {
						for ( SpotFleetRequestConfig spotFleetRequestConfig : client.describeSpotFleetRequests()
								.spotFleetRequestConfigs() ) {
							if ( spotFleetRequestConfig.spotFleetRequestConfig() != null
									&& spotFleetRequestConfig.spotFleetRequestConfig().launchSpecifications() != null ) {
								for ( SpotFleetLaunchSpecification launchSpecification : spotFleetRequestConfig.spotFleetRequestConfig().launchSpecifications() ) {
									list.add( new AwsEc2SpotFleetRequestLaunchSpecification(
											account.getAccountId(),
											region.id(),
											spotFleetRequestConfig.spotFleetRequestId(),
											launchSpecification
									) );
								}
							}
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch spot fleet request launch specification list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsEc2SpotFleetRequestLaunchSpecification.class, AwsConventionContext.INSTANCE );
	}
}

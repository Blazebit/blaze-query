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
import software.amazon.awssdk.services.ec2.model.GetLaunchTemplateDataRequest;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.ResponseLaunchTemplateData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches launch template data derived from existing instances.
 *
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class LaunchTemplateDataFromInstanceDataFetcher implements DataFetcher<AwsLaunchTemplateData>, Serializable {

	public static final LaunchTemplateDataFromInstanceDataFetcher INSTANCE =
			new LaunchTemplateDataFromInstanceDataFetcher();

	private LaunchTemplateDataFromInstanceDataFetcher() {
	}

	@Override
	public List<AwsLaunchTemplateData> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsLaunchTemplateData> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					Ec2ClientBuilder ec2ClientBuilder = Ec2Client.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ec2ClientBuilder.httpClient( sdkHttpClient );
					}
					try (Ec2Client client = ec2ClientBuilder.build()) {
						for ( Reservation reservation : client.describeInstances().reservations() ) {
							for ( Instance instance : reservation.instances() ) {
								ResponseLaunchTemplateData launchTemplateData = client.getLaunchTemplateData(
										GetLaunchTemplateDataRequest.builder()
												.instanceId( instance.instanceId() )
												.build()
								).launchTemplateData();
								if ( launchTemplateData != null ) {
									list.add( new AwsLaunchTemplateData(
											account.getAccountId(),
											region.id(),
											instance.instanceId(),
											launchTemplateData
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
			throw new DataFetcherException( "Could not fetch launch template data list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsLaunchTemplateData.class, AwsConventionContext.INSTANCE );
	}
}

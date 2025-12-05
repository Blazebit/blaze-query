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
import software.amazon.awssdk.services.ec2.model.GetConsoleOutputRequest;
import software.amazon.awssdk.services.ec2.model.GetConsoleOutputResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class LatestConsoleOutputDataFetcher implements DataFetcher<AwsLatestConsoleOutput>, Serializable {

	public static final LatestConsoleOutputDataFetcher INSTANCE = new LatestConsoleOutputDataFetcher();

	private LatestConsoleOutputDataFetcher() {
	}

	@Override
	public List<AwsLatestConsoleOutput> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsLatestConsoleOutput> list = new ArrayList<>();
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
								GetConsoleOutputResponse consoleOutputResponse = client.getConsoleOutput(
										GetConsoleOutputRequest.builder()
												.instanceId( instance.instanceId() )
												.latest( true )
												.build()
								);
								String ownerId = reservation.ownerId();
								list.add( new AwsLatestConsoleOutput(
										ownerId == null ? account.getAccountId() : ownerId,
										region.id(),
										instance.instanceId(),
										consoleOutputResponse
								) );
							}
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch latest console output list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsLatestConsoleOutput.class, AwsConventionContext.INSTANCE );
	}
}

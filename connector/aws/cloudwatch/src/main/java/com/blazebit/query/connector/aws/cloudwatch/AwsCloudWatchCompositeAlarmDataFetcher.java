/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.cloudwatch;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClientBuilder;
import software.amazon.awssdk.services.cloudwatch.model.CompositeAlarm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsCloudWatchCompositeAlarmDataFetcher implements DataFetcher<AwsCloudWatchCompositeAlarm>, Serializable {

	public static final AwsCloudWatchCompositeAlarmDataFetcher INSTANCE = new AwsCloudWatchCompositeAlarmDataFetcher();

	private AwsCloudWatchCompositeAlarmDataFetcher() {
	}

	@Override
	public List<AwsCloudWatchCompositeAlarm> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsCloudWatchCompositeAlarm> list = new ArrayList<>();

			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					CloudWatchClientBuilder clientBuilder = CloudWatchClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						clientBuilder.httpClient( sdkHttpClient );
					}
					try (CloudWatchClient client = clientBuilder.build()) {
						for ( var page : client.describeAlarmsPaginator() ) {
							for ( CompositeAlarm alarm : page.compositeAlarms() ) {
								list.add( new AwsCloudWatchCompositeAlarm(
										alarm.alarmArn(),
										alarm
								) );
							}
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch composite alarms", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsCloudWatchCompositeAlarm.class, AwsConventionContext.INSTANCE );
	}
}

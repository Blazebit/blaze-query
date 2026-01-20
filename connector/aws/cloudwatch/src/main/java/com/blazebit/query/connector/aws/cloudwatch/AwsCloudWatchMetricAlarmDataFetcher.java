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
import software.amazon.awssdk.services.cloudwatch.model.MetricAlarm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsCloudWatchMetricAlarmDataFetcher implements DataFetcher<AwsCloudWatchMetricAlarm>, Serializable {

	public static final AwsCloudWatchMetricAlarmDataFetcher INSTANCE = new AwsCloudWatchMetricAlarmDataFetcher();

	private AwsCloudWatchMetricAlarmDataFetcher() {
	}

	@Override
	public List<AwsCloudWatchMetricAlarm> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsCloudWatchMetricAlarm> list = new ArrayList<>();

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
							for ( MetricAlarm alarm : page.metricAlarms() ) {
								list.add( new AwsCloudWatchMetricAlarm(
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
			throw new DataFetcherException( "Could not fetch metric alarms", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsCloudWatchMetricAlarm.class, AwsConventionContext.INSTANCE );
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.cloudwatchlogs;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClientBuilder;
import software.amazon.awssdk.services.cloudwatchlogs.model.MetricFilter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsCloudWatchLogsMetricFilterDataFetcher implements DataFetcher<AwsCloudWatchLogsMetricFilter>, Serializable {

	public static final AwsCloudWatchLogsMetricFilterDataFetcher INSTANCE = new AwsCloudWatchLogsMetricFilterDataFetcher();

	private AwsCloudWatchLogsMetricFilterDataFetcher() {
	}

	@Override
	public List<AwsCloudWatchLogsMetricFilter> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsCloudWatchLogsMetricFilter> list = new ArrayList<>();

			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					CloudWatchLogsClientBuilder clientBuilder = CloudWatchLogsClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						clientBuilder.httpClient( sdkHttpClient );
					}
					try (CloudWatchLogsClient client = clientBuilder.build()) {
						for ( var page : client.describeMetricFiltersPaginator() ) {
							for ( MetricFilter filter : page.metricFilters() ) {
								list.add( new AwsCloudWatchLogsMetricFilter(
										account.getAccountId(),
										region.id(),
										filter.filterName(),
										filter
								) );
							}
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch metric filters", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsCloudWatchLogsMetricFilter.class, AwsConventionContext.INSTANCE );
	}
}

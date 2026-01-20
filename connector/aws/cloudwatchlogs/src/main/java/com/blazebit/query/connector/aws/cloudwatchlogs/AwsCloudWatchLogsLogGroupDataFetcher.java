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
import software.amazon.awssdk.services.cloudwatchlogs.model.LogGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsCloudWatchLogsLogGroupDataFetcher implements DataFetcher<AwsCloudWatchLogsLogGroup>, Serializable {

	public static final AwsCloudWatchLogsLogGroupDataFetcher INSTANCE = new AwsCloudWatchLogsLogGroupDataFetcher();

	private AwsCloudWatchLogsLogGroupDataFetcher() {
	}

	@Override
	public List<AwsCloudWatchLogsLogGroup> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsCloudWatchLogsLogGroup> list = new ArrayList<>();

			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					CloudWatchLogsClientBuilder clientBuilder = CloudWatchLogsClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						clientBuilder.httpClient( sdkHttpClient );
					}
					try (CloudWatchLogsClient client = clientBuilder.build()) {
						for ( var page : client.describeLogGroupsPaginator() ) {
							for ( LogGroup logGroup : page.logGroups() ) {
								list.add( new AwsCloudWatchLogsLogGroup(
										logGroup.arn(),
										logGroup
								) );
							}
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch log groups", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsCloudWatchLogsLogGroup.class, AwsConventionContext.INSTANCE );
	}
}

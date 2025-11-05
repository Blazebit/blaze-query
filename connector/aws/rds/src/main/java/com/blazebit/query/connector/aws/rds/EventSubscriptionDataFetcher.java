/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.rds;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.RdsClientBuilder;
import software.amazon.awssdk.services.rds.model.EventSubscription;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class EventSubscriptionDataFetcher implements DataFetcher<AwsEventSubscription>, Serializable {

	public static final EventSubscriptionDataFetcher INSTANCE = new EventSubscriptionDataFetcher();

	private EventSubscriptionDataFetcher() {
	}

	@Override
	public List<AwsEventSubscription> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsEventSubscription> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					RdsClientBuilder rdsClientBuilder = RdsClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						rdsClientBuilder.httpClient( sdkHttpClient );
					}
					try (RdsClient client = rdsClientBuilder.build()) {
						for ( EventSubscription eventSubscription : client.describeEventSubscriptions()
								.eventSubscriptionsList() ) {
							list.add( new AwsEventSubscription( eventSubscription.eventSubscriptionArn(),
									eventSubscription ) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch event subscription list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsEventSubscription.class, AwsConventionContext.INSTANCE );
	}
}

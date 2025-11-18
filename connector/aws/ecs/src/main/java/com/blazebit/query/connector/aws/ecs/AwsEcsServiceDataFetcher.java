/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ecs;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.EcsClientBuilder;
import software.amazon.awssdk.services.ecs.model.DescribeServicesRequest;
import software.amazon.awssdk.services.ecs.model.DescribeServicesResponse;
import software.amazon.awssdk.services.ecs.model.Service;
import software.amazon.awssdk.services.ecs.model.ServiceField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEcsServiceDataFetcher implements DataFetcher<AwsEcsService>, Serializable {

	public static final AwsEcsServiceDataFetcher INSTANCE = new AwsEcsServiceDataFetcher();

	private AwsEcsServiceDataFetcher() {
	}

	@Override
	public List<AwsEcsService> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsEcsService> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					EcsClientBuilder ecsClientBuilder = EcsClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ecsClientBuilder.httpClient( sdkHttpClient );
					}
					try (EcsClient client = ecsClientBuilder.build()) {
						List<String> clusterArns = client.listClusters().clusterArns();
						for ( String clusterArn : clusterArns ) {
							List<String> serviceArns = client.listServices( r -> r.cluster( clusterArn ) )
									.serviceArns();
							if ( !serviceArns.isEmpty() ) {
								// AWS DescribeServices API accepts up to 10 services per request
								int batchSize = 10;
								for ( int i = 0; i < serviceArns.size(); i += batchSize ) {
									int end = Math.min( i + batchSize, serviceArns.size() );
									List<String> batch = serviceArns.subList( i, end );
									DescribeServicesRequest request = DescribeServicesRequest.builder()
											.cluster( clusterArn )
											.services( batch )
											.include( ServiceField.TAGS )
											.build();
									DescribeServicesResponse response = client.describeServices( request );
									for ( Service service : response.services() ) {
										list.add( new AwsEcsService( service.serviceArn(), service ) );
									}
								}
							}
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch service list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsEcsService.class, AwsConventionContext.INSTANCE );
	}
}

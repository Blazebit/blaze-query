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
import software.amazon.awssdk.services.ecs.model.DescribeTaskSetsResponse;
import software.amazon.awssdk.services.ecs.model.TaskSet;
import software.amazon.awssdk.services.ecs.model.TaskSetField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class TaskSetDataFetcher implements DataFetcher<AwsEcsTaskSet>, Serializable {

	public static final TaskSetDataFetcher INSTANCE = new TaskSetDataFetcher();

	private TaskSetDataFetcher() {
	}

	@Override
	public List<AwsEcsTaskSet> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<? extends AwsEcsService> services = context.getSession().getOrFetch( AwsEcsService.class );
			List<AwsEcsTaskSet> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					EcsClientBuilder ecsClientBuilder = EcsClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ecsClientBuilder.httpClient( sdkHttpClient );
					}
					try (EcsClient client = ecsClientBuilder.build()) {
						for ( AwsEcsService service : services ) {
							if ( service.getAccountId().equals( account.getAccountId() )
									&& service.getRegionId().equals( region.id() ) ) {
								DescribeTaskSetsResponse response = client.describeTaskSets( r -> r
										.cluster( service.getPayload().clusterArn() )
										.service( service.getPayload().serviceArn() )
										.include( TaskSetField.TAGS )
								);
								for ( TaskSet taskSet : response.taskSets() ) {
									list.add( new AwsEcsTaskSet( taskSet.taskSetArn(), taskSet ) );
								}
							}
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch task set list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsEcsTaskSet.class, AwsConventionContext.INSTANCE );
	}
}

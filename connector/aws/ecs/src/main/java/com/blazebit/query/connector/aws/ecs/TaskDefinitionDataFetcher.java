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
import software.amazon.awssdk.services.ecs.model.DescribeTaskDefinitionRequest;
import software.amazon.awssdk.services.ecs.model.DescribeTaskDefinitionResponse;
import software.amazon.awssdk.services.ecs.model.TaskDefinitionField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class TaskDefinitionDataFetcher implements DataFetcher<AwsTaskDefinition>, Serializable {

	public static final TaskDefinitionDataFetcher INSTANCE = new TaskDefinitionDataFetcher();

	private TaskDefinitionDataFetcher() {
	}

	@Override
	public List<AwsTaskDefinition> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsTaskDefinition> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					EcsClientBuilder ecsClientBuilder = EcsClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ecsClientBuilder.httpClient( sdkHttpClient );
					}
					try (EcsClient client = ecsClientBuilder.build()) {
						List<String> taskDefinitionArns = client.listTaskDefinitions().taskDefinitionArns();
						for ( String taskDefinitionArn : taskDefinitionArns ) {
							DescribeTaskDefinitionRequest request = DescribeTaskDefinitionRequest.builder()
									.taskDefinition( taskDefinitionArn )
									.include( TaskDefinitionField.TAGS )
									.build();
							DescribeTaskDefinitionResponse response = client.describeTaskDefinition( request );
							list.add(
									new AwsTaskDefinition( response.taskDefinition().taskDefinitionArn(), response ) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch task definition list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsTaskDefinition.class, AwsConventionContext.INSTANCE );
	}
}

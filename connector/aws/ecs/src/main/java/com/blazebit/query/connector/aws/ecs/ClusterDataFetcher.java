/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ecs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.EcsClientBuilder;
import software.amazon.awssdk.services.ecs.model.Cluster;
import software.amazon.awssdk.services.ecs.model.DescribeClustersRequest;
import software.amazon.awssdk.services.ecs.model.DescribeClustersResponse;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ClusterDataFetcher implements DataFetcher<Cluster>, Serializable {

	public static final ClusterDataFetcher INSTANCE = new ClusterDataFetcher();

	private ClusterDataFetcher() {
	}

	@Override
	public List<Cluster> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<Cluster> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				EcsClientBuilder ec2ClientBuilder = EcsClient.builder()
						.region( account.getRegion() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					ec2ClientBuilder.httpClient( sdkHttpClient );
				}
				try (EcsClient client = ec2ClientBuilder.build()) {
					List<String> clusters = client.listClusters().clusterArns();
					DescribeClustersRequest request = DescribeClustersRequest.builder().clusters( clusters ).build();
					DescribeClustersResponse describeClustersResponse = client.describeClusters( request );
					list.addAll( describeClustersResponse.clusters() );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch instance list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( Cluster.class, AwsConventionContext.INSTANCE );
	}
}

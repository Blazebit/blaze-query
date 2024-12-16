/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.elb;

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
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2ClientBuilder;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class LoadBalancerDataFetcher implements DataFetcher<AwsLoadBalancer>, Serializable {

	public static final LoadBalancerDataFetcher INSTANCE = new LoadBalancerDataFetcher();

	private LoadBalancerDataFetcher() {
	}

	@Override
	public List<AwsLoadBalancer> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsLoadBalancer> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					ElasticLoadBalancingV2ClientBuilder ec2ClientBuilder = ElasticLoadBalancingV2Client.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ec2ClientBuilder.httpClient( sdkHttpClient );
					}
					try (ElasticLoadBalancingV2Client client = ec2ClientBuilder.build()) {
						for ( LoadBalancer loadBalancer : client.describeLoadBalancers().loadBalancers() ) {
							list.add( new AwsLoadBalancer(
									loadBalancer.loadBalancerArn(),
									loadBalancer
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch elastic load balancer list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsLoadBalancer.class, AwsConventionContext.INSTANCE );
	}
}

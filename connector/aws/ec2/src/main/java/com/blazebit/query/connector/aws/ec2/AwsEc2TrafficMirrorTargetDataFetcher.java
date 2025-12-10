/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.Ec2ClientBuilder;
import software.amazon.awssdk.services.ec2.model.TrafficMirrorTarget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2TrafficMirrorTargetDataFetcher implements DataFetcher<AwsEc2TrafficMirrorTarget>, Serializable {

	public static final AwsEc2TrafficMirrorTargetDataFetcher INSTANCE = new AwsEc2TrafficMirrorTargetDataFetcher();

	private AwsEc2TrafficMirrorTargetDataFetcher() {
	}

	@Override
	public List<AwsEc2TrafficMirrorTarget> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsEc2TrafficMirrorTarget> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					Ec2ClientBuilder ec2ClientBuilder = Ec2Client.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ec2ClientBuilder.httpClient( sdkHttpClient );
					}
					try (Ec2Client client = ec2ClientBuilder.build()) {
						for ( TrafficMirrorTarget trafficMirrorTarget : client.describeTrafficMirrorTargets( r -> {
						} ).trafficMirrorTargets() ) {
							list.add( new AwsEc2TrafficMirrorTarget(
									account.getAccountId(),
									region.id(),
									trafficMirrorTarget.trafficMirrorTargetId(),
									trafficMirrorTarget
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch traffic mirror target list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsEc2TrafficMirrorTarget.class, AwsConventionContext.INSTANCE );
	}
}

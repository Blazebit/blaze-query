/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.cloudtrail;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClient;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClientBuilder;
import software.amazon.awssdk.services.cloudtrail.model.GetTrailStatusResponse;
import software.amazon.awssdk.services.cloudtrail.model.Trail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsCloudTrailTrailStatusDataFetcher implements DataFetcher<AwsCloudTrailTrailStatus>, Serializable {

	public static final AwsCloudTrailTrailStatusDataFetcher INSTANCE = new AwsCloudTrailTrailStatusDataFetcher();

	private AwsCloudTrailTrailStatusDataFetcher() {
	}

	@Override
	public List<AwsCloudTrailTrailStatus> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsCloudTrailTrailStatus> list = new ArrayList<>();

			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					CloudTrailClientBuilder clientBuilder = CloudTrailClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						clientBuilder.httpClient( sdkHttpClient );
					}
					try (CloudTrailClient client = clientBuilder.build()) {
						for ( Trail trail : client.describeTrails().trailList() ) {
							GetTrailStatusResponse status = client.getTrailStatus( r -> r.name( trail.trailARN() ) );
							list.add( new AwsCloudTrailTrailStatus(
									trail.trailARN(),
									status
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch trail statuses", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsCloudTrailTrailStatus.class, AwsConventionContext.INSTANCE );
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.GetBucketVersioningRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class BucketVersioningFetcher implements DataFetcher<AwsBucketVersioning>, Serializable {

	public static final BucketVersioningFetcher INSTANCE = new BucketVersioningFetcher();

	private BucketVersioningFetcher() {
	}

	@Override
	public List<AwsBucketVersioning> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsBucketVersioning> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					S3ClientBuilder s3ClientBuilder = S3Client.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						s3ClientBuilder.httpClient( sdkHttpClient );
					}
					try (S3Client client = s3ClientBuilder.build()) {
						for ( Bucket bucket : client.listBuckets().buckets() ) {
							var bucketVersioning = client.getBucketVersioning(
									GetBucketVersioningRequest.builder().bucket( bucket.name() )
											.build() );
							list.add( new AwsBucketVersioning(
									account.getAccountId(),
									region.id(),
									bucket.name(),
									bucketVersioning
							) );
						}
					}
				}
			}
			return list;
		}
		catch (Exception e) {
			throw new DataFetcherException( "Could not fetch bucket versioning list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsBucketVersioning.class, AwsConventionContext.INSTANCE );
	}
}

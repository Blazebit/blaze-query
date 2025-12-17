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
import software.amazon.awssdk.services.s3.model.GetBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class BucketPolicyFetcher implements DataFetcher<AwsBucketPolicy>, Serializable {

	public static final BucketPolicyFetcher INSTANCE = new BucketPolicyFetcher();

	private BucketPolicyFetcher() {
	}

	@Override
	public List<AwsBucketPolicy> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsBucketPolicy> list = new ArrayList<>();
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
							try {
								var bucketPolicy = client.getBucketPolicy(
										GetBucketPolicyRequest.builder().bucket( bucket.name() )
												.build() );
								list.add( new AwsBucketPolicy(
										account.getAccountId(),
										region.id(),
										bucket.name(),
										bucketPolicy.policy()
								) );
							}
							catch (S3Exception e) {
								if ( Objects.equals( e.awsErrorDetails().errorCode(),
										"NoSuchBucketPolicy" ) ) {
									continue;
								}
								throw e;
							}
						}
					}
				}
			}
			return list;
		}
		catch (Exception e) {
			throw new DataFetcherException( "Could not fetch bucket policy list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsBucketPolicy.class, AwsConventionContext.INSTANCE );
	}
}

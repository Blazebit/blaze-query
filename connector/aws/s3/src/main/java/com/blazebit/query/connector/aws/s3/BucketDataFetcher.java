/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.s3;

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
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.Bucket;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class BucketDataFetcher implements DataFetcher<Bucket>, Serializable {

	public static final BucketDataFetcher INSTANCE = new BucketDataFetcher();

	private BucketDataFetcher() {
	}

	@Override
	public List<Bucket> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<Bucket> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				S3ClientBuilder ec2ClientBuilder = S3Client.builder()
						.region( account.getRegion() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					ec2ClientBuilder.httpClient( sdkHttpClient );
				}
				try (S3Client client = ec2ClientBuilder.build()) {
					list.addAll( client.listBuckets().buckets() );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch bucket list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( Bucket.class, AwsConventionContext.INSTANCE );
	}
}

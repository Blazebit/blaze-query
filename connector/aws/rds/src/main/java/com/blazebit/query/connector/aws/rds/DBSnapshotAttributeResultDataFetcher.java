/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.rds;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.RdsClientBuilder;
import software.amazon.awssdk.services.rds.model.DBSnapshot;
import software.amazon.awssdk.services.rds.model.DescribeDbSnapshotAttributesRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DBSnapshotAttributeResultDataFetcher implements DataFetcher<AwsDBSnapshotAttributeResult>, Serializable {

	public static final DBSnapshotAttributeResultDataFetcher INSTANCE = new DBSnapshotAttributeResultDataFetcher();

	private DBSnapshotAttributeResultDataFetcher() {
	}

	@Override
	public List<AwsDBSnapshotAttributeResult> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsDBSnapshotAttributeResult> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					RdsClientBuilder rdsClientBuilder = RdsClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						rdsClientBuilder.httpClient( sdkHttpClient );
					}
					try (RdsClient client = rdsClientBuilder.build()) {
						for ( DBSnapshot dbInstance : client.describeDBSnapshots().dbSnapshots() ) {
							var bucketPolicy = client.describeDBSnapshotAttributes(
									DescribeDbSnapshotAttributesRequest.builder().dbSnapshotIdentifier(dbInstance.dbSnapshotIdentifier() )
											.build() );
							list.add( new AwsDBSnapshotAttributeResult( dbInstance.dbSnapshotArn(), bucketPolicy.dbSnapshotAttributesResult() ) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch db instance list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsDBSnapshotAttributeResult.class, AwsConventionContext.INSTANCE );
	}
}

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
import software.amazon.awssdk.services.rds.model.DBClusterSnapshot;
import software.amazon.awssdk.services.rds.model.DBClusterSnapshotAttribute;
import software.amazon.awssdk.services.rds.model.DescribeDbClusterSnapshotAttributesRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class DBClusterSnapshotAttributeDataFetcher implements DataFetcher<AwsDBClusterSnapshotAttribute>, Serializable {

	public static final DBClusterSnapshotAttributeDataFetcher INSTANCE = new DBClusterSnapshotAttributeDataFetcher();

	private DBClusterSnapshotAttributeDataFetcher() {
	}

	@Override
	public List<AwsDBClusterSnapshotAttribute> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsDBClusterSnapshotAttribute> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					RdsClientBuilder rdsClientBuilder = RdsClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						rdsClientBuilder.httpClient( sdkHttpClient );
					}
					try (RdsClient client = rdsClientBuilder.build()) {
						for ( DBClusterSnapshot dbInstance : client.describeDBClusterSnapshots().dbClusterSnapshots() ) {
							var bucketPolicy = client.describeDBClusterSnapshotAttributes(
									DescribeDbClusterSnapshotAttributesRequest.builder()
											.dbClusterSnapshotIdentifier( dbInstance.dbClusterSnapshotIdentifier() )
											.build() );
							for ( DBClusterSnapshotAttribute snapshotAttribute : bucketPolicy.dbClusterSnapshotAttributesResult()
									.dbClusterSnapshotAttributes() ) {
								list.add( new AwsDBClusterSnapshotAttribute( dbInstance.dbClusterSnapshotArn(),
										snapshotAttribute ) );
							}

						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch db cluster snapshot attribute list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsDBClusterSnapshotAttribute.class,
				AwsConventionContext.INSTANCE );
	}
}

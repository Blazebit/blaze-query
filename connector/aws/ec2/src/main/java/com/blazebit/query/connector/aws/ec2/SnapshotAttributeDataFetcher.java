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
import software.amazon.awssdk.services.ec2.model.DescribeSnapshotAttributeRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSnapshotAttributeResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSnapshotsRequest;
import software.amazon.awssdk.services.ec2.model.Snapshot;
import software.amazon.awssdk.services.ec2.model.SnapshotAttributeName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class SnapshotAttributeDataFetcher implements DataFetcher<AwsSnapshotAttribute>, Serializable {

	public static final SnapshotAttributeDataFetcher INSTANCE = new SnapshotAttributeDataFetcher();

	private SnapshotAttributeDataFetcher() {
	}

	@Override
	public List<AwsSnapshotAttribute> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsSnapshotAttribute> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					Ec2ClientBuilder ec2ClientBuilder = Ec2Client.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ec2ClientBuilder.httpClient( sdkHttpClient );
					}
					try (Ec2Client client = ec2ClientBuilder.build()) {
						for ( Snapshot snapshot : client.describeSnapshots( DescribeSnapshotsRequest.builder()
								.ownerIds(
										accounts.stream().map( AwsConnectorConfig.Account::getAccountId ).toList() )
								.build() ).snapshots() ) {
							DescribeSnapshotAttributeResponse snapshotAttributeResponse = client.describeSnapshotAttribute(
									DescribeSnapshotAttributeRequest.builder()
											.snapshotId( snapshot.snapshotId() )
											.attribute( SnapshotAttributeName.CREATE_VOLUME_PERMISSION )
											.build()
							);
							String ownerId = snapshot.ownerId();
							list.add( new AwsSnapshotAttribute(
									ownerId == null ? account.getAccountId() : ownerId,
									region.id(),
									snapshot.snapshotId(),
									snapshotAttributeResponse
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch snapshot attribute list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsSnapshotAttribute.class, AwsConventionContext.INSTANCE );
	}
}

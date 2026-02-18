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
import software.amazon.awssdk.services.ec2.model.DescribeManagedPrefixListsRequest;
import software.amazon.awssdk.services.ec2.model.ManagedPrefixList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2ManagedPrefixListDataFetcher implements DataFetcher<AwsEc2ManagedPrefixList>, Serializable {

	public static final AwsEc2ManagedPrefixListDataFetcher INSTANCE = new AwsEc2ManagedPrefixListDataFetcher();

	private AwsEc2ManagedPrefixListDataFetcher() {
	}

	@Override
	public List<AwsEc2ManagedPrefixList> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsEc2ManagedPrefixList> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					Ec2ClientBuilder ec2ClientBuilder = Ec2Client.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ec2ClientBuilder.httpClient( sdkHttpClient );
					}
					try (Ec2Client client = ec2ClientBuilder.build()) {
						DescribeManagedPrefixListsRequest request = DescribeManagedPrefixListsRequest.builder().build();
						for ( ManagedPrefixList prefixList : client.describeManagedPrefixLists( request ).prefixLists() ) {
							list.add( new AwsEc2ManagedPrefixList(
									account.getAccountId(),
									region.id(),
									prefixList.prefixListId(),
									prefixList
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch managed prefix list list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsEc2ManagedPrefixList.class, AwsConventionContext.INSTANCE );
	}
}

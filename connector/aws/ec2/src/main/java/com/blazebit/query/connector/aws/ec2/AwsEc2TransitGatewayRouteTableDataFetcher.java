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
import software.amazon.awssdk.services.ec2.model.TransitGatewayRouteTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2TransitGatewayRouteTableDataFetcher implements DataFetcher<AwsEc2TransitGatewayRouteTable>, Serializable {

	public static final AwsEc2TransitGatewayRouteTableDataFetcher INSTANCE = new AwsEc2TransitGatewayRouteTableDataFetcher();

	private AwsEc2TransitGatewayRouteTableDataFetcher() {
	}

	@Override
	public List<AwsEc2TransitGatewayRouteTable> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsEc2TransitGatewayRouteTable> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					Ec2ClientBuilder ec2ClientBuilder = Ec2Client.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ec2ClientBuilder.httpClient( sdkHttpClient );
					}
					try (Ec2Client client = ec2ClientBuilder.build()) {
						for ( TransitGatewayRouteTable routeTable : client.describeTransitGatewayRouteTables()
								.transitGatewayRouteTables() ) {
							list.add( new AwsEc2TransitGatewayRouteTable(
									account.getAccountId(),
									region.id(),
									routeTable.transitGatewayRouteTableId(),
									routeTable
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch transit gateway route table list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsEc2TransitGatewayRouteTable.class,
				AwsConventionContext.INSTANCE );
	}
}

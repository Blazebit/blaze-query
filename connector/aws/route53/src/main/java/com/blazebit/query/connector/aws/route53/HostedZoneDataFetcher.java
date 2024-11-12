/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.route53;

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
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.Route53ClientBuilder;
import software.amazon.awssdk.services.route53.model.HostedZone;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class HostedZoneDataFetcher implements DataFetcher<HostedZone>, Serializable {

	public static final HostedZoneDataFetcher INSTANCE = new HostedZoneDataFetcher();

	private HostedZoneDataFetcher() {
	}

	@Override
	public List<HostedZone> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<HostedZone> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				Route53ClientBuilder ec2ClientBuilder = Route53Client.builder()
						.region( account.getRegion() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					ec2ClientBuilder.httpClient( sdkHttpClient );
				}
				try (Route53Client client = ec2ClientBuilder.build()) {
					list.addAll( client.listHostedZones().hostedZones() );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch hosted zones list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( HostedZone.class, AwsConventionContext.INSTANCE );
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

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
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.Ec2ClientBuilder;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class InstanceDataFetcher implements DataFetcher<Instance>, Serializable {

	public static final InstanceDataFetcher INSTANCE = new InstanceDataFetcher();

	private InstanceDataFetcher() {
	}

	@Override
	public List<Instance> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<Instance> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				Ec2ClientBuilder ec2ClientBuilder = Ec2Client.builder()
						.region( account.getRegion() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					ec2ClientBuilder.httpClient( sdkHttpClient );
				}
				try (Ec2Client client = ec2ClientBuilder.build()) {
					for ( Reservation reservation : client.describeInstances().reservations() ) {
						list.addAll( reservation.instances() );
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch instance list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( Instance.class, AwsConventionContext.INSTANCE );
	}
}

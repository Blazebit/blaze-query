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
import software.amazon.awssdk.services.route53.model.HealthCheck;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class HealthCheckDataFetcher implements DataFetcher<AwsHealthCheck>, Serializable {

	public static final HealthCheckDataFetcher INSTANCE = new HealthCheckDataFetcher();

	private HealthCheckDataFetcher() {
	}

	@Override
	public List<AwsHealthCheck> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsHealthCheck> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				Route53ClientBuilder ec2ClientBuilder = Route53Client.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					ec2ClientBuilder.httpClient( sdkHttpClient );
				}
				try (Route53Client client = ec2ClientBuilder.build()) {
					for ( HealthCheck healthCheck : client.listHealthChecks().healthChecks() ) {
						list.add( new AwsHealthCheck(
								account.getAccountId(),
								healthCheck.id(),
								healthCheck
						) );
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch health check list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsHealthCheck.class, AwsConventionContext.INSTANCE );
	}
}

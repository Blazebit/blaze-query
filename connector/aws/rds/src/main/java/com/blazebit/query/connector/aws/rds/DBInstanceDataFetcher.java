/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.rds;

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
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.RdsClientBuilder;
import software.amazon.awssdk.services.rds.model.DBInstance;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DBInstanceDataFetcher implements DataFetcher<DBInstance>, Serializable {

	public static final DBInstanceDataFetcher INSTANCE = new DBInstanceDataFetcher();

	private DBInstanceDataFetcher() {
	}

	@Override
	public List<DBInstance> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<DBInstance> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				RdsClientBuilder ec2ClientBuilder = RdsClient.builder()
						.region( account.getRegion() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					ec2ClientBuilder.httpClient( sdkHttpClient );
				}
				try (RdsClient client = ec2ClientBuilder.build()) {
					list.addAll( client.describeDBInstances().dbInstances() );
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
		return DataFormats.componentMethodConvention( DBInstance.class, AwsConventionContext.INSTANCE );
	}
}

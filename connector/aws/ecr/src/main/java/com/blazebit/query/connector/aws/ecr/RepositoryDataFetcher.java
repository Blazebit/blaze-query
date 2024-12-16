/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ecr;

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
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.EcrClientBuilder;
import software.amazon.awssdk.services.ecr.model.Repository;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class RepositoryDataFetcher implements DataFetcher<AwsRepository>, Serializable {

	public static final RepositoryDataFetcher INSTANCE = new RepositoryDataFetcher();

	private RepositoryDataFetcher() {
	}

	@Override
	public List<AwsRepository> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsRepository> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					EcrClientBuilder ec2ClientBuilder = EcrClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ec2ClientBuilder.httpClient( sdkHttpClient );
					}
					try (EcrClient client = ec2ClientBuilder.build()) {
						for ( Repository repository : client.describeRepositories().repositories() ) {
							list.add( new AwsRepository(
								repository.repositoryArn(),
								repository
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch repository list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsRepository.class, AwsConventionContext.INSTANCE );
	}
}

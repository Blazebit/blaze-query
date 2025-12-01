/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.accessanalyzer;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.accessanalyzer.AccessAnalyzerClient;
import software.amazon.awssdk.services.accessanalyzer.AccessAnalyzerClientBuilder;
import software.amazon.awssdk.services.accessanalyzer.model.AnalyzerSummary;
import software.amazon.awssdk.services.accessanalyzer.model.ListAnalyzersRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AccessAnalyzerAnalyzerDataFetcher implements DataFetcher<AwsAccessAnalyzerAnalyzer>, Serializable {

	public static final AccessAnalyzerAnalyzerDataFetcher INSTANCE = new AccessAnalyzerAnalyzerDataFetcher();

	private AccessAnalyzerAnalyzerDataFetcher() {
	}

	@Override
	public List<AwsAccessAnalyzerAnalyzer> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsAccessAnalyzerAnalyzer> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					AccessAnalyzerClientBuilder clientBuilder = AccessAnalyzerClient.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						clientBuilder.httpClient( sdkHttpClient );
					}
					try (AccessAnalyzerClient client = clientBuilder.build()) {
						for ( AnalyzerSummary analyzer : client.listAnalyzersPaginator( ListAnalyzersRequest.builder().build() ).analyzers() ) {
							StringTokenizer tokenizer = new StringTokenizer( analyzer.arn(), ":" );
							// arn
							tokenizer.nextToken();
							// aws
							tokenizer.nextToken();
							// access-analyzer
							tokenizer.nextToken();
							// region
							tokenizer.nextToken();
							// account id
							tokenizer.nextToken();
							// resource id
							String resourceId = tokenizer.nextToken();

							list.add( new AwsAccessAnalyzerAnalyzer(
									account.getAccountId(),
									region.id(),
									resourceId,
									analyzer
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch analyzer list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsAccessAnalyzerAnalyzer.class, AwsConventionContext.INSTANCE );
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.connector.aws.base.AwsConnectorConfig;
import com.blazebit.query.connector.aws.base.AwsConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.IamClientBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martijn Sprengers
 * @since 1.0.0
 */
public class AccountSummaryDataFetcher implements DataFetcher<AccountSummary>, Serializable {

	public static final AccountSummaryDataFetcher INSTANCE = new AccountSummaryDataFetcher();

	private AccountSummaryDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AccountSummary.class, AwsConventionContext.INSTANCE );
	}

    @Override
    public List<AccountSummary> fetch(DataFetchContext context) {
        try {
            List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
            SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
            List<AccountSummary> list = new ArrayList<>();
            for ( AwsConnectorConfig.Account account : accounts) {
                IamClientBuilder iamClientBuilder = IamClient.builder()
                        .region( account.getRegion() )
                        .credentialsProvider( account.getCredentialsProvider() );
                if ( sdkHttpClient != null ) {
                    iamClientBuilder.httpClient( sdkHttpClient );
                }
                try (IamClient client = iamClientBuilder.build()) {
                    list.add( new AccountSummary( client.getAccountSummary().summaryMap() ) );
                }
            }
            return list;
        } catch (RuntimeException e) {
            throw new DataFetcherException("Could not fetch account summary", e);
        }
    }
}

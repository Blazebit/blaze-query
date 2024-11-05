/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.efs;

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
import software.amazon.awssdk.services.efs.EfsClient;
import software.amazon.awssdk.services.efs.EfsClientBuilder;
import software.amazon.awssdk.services.efs.model.FileSystemDescription;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class FileSystemDataFetcher implements DataFetcher<FileSystemDescription>, Serializable {

	public static final FileSystemDataFetcher INSTANCE = new FileSystemDataFetcher();

	private FileSystemDataFetcher() {
	}

	@Override
	public List<FileSystemDescription> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<FileSystemDescription> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				EfsClientBuilder ec2ClientBuilder = EfsClient.builder()
						.region( account.getRegion() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					ec2ClientBuilder.httpClient( sdkHttpClient );
				}
				try (EfsClient client = ec2ClientBuilder.build()) {
					list.addAll( client.describeFileSystems().fileSystems() );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch file systems list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( FileSystemDescription.class, AwsConventionContext.INSTANCE );
	}
}

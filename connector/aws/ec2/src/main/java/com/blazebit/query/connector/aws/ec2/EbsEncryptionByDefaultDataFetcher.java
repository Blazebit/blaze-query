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
import software.amazon.awssdk.services.ec2.model.GetEbsEncryptionByDefaultRequest;
import software.amazon.awssdk.services.ec2.model.GetEbsEncryptionByDefaultResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class EbsEncryptionByDefaultDataFetcher implements DataFetcher<AwsEbsEncryptionByDefault>, Serializable {

	public static final EbsEncryptionByDefaultDataFetcher INSTANCE = new EbsEncryptionByDefaultDataFetcher();

	private EbsEncryptionByDefaultDataFetcher() {
	}

	@Override
	public List<AwsEbsEncryptionByDefault> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsEbsEncryptionByDefault> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					Ec2ClientBuilder ec2ClientBuilder = Ec2Client.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ec2ClientBuilder.httpClient( sdkHttpClient );
					}
					try (Ec2Client client = ec2ClientBuilder.build()) {
						GetEbsEncryptionByDefaultResponse response = client.getEbsEncryptionByDefault(
								GetEbsEncryptionByDefaultRequest.builder().build()
						);
						list.add( new AwsEbsEncryptionByDefault(
								account.getAccountId(),
								region.id(),
								region.id() + ":ebs-encryption-by-default",
								response
						) );
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch ebs encryption by default list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsEbsEncryptionByDefault.class, AwsConventionContext.INSTANCE );
	}
}

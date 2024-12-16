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
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.Ec2ClientBuilder;
import software.amazon.awssdk.services.ec2.model.NetworkAcl;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class NetworkAclDataFetcher implements DataFetcher<AwsNetworkAcl>, Serializable {

	public static final NetworkAclDataFetcher INSTANCE = new NetworkAclDataFetcher();

	private NetworkAclDataFetcher() {
	}

	@Override
	public List<AwsNetworkAcl> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsNetworkAcl> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					Ec2ClientBuilder ec2ClientBuilder = Ec2Client.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ec2ClientBuilder.httpClient( sdkHttpClient );
					}
					try (Ec2Client client = ec2ClientBuilder.build()) {
						for ( NetworkAcl networkAcl : client.describeNetworkAcls().networkAcls() ) {
							list.add( new AwsNetworkAcl(
									networkAcl.ownerId(),
									region.id(),
									networkAcl.networkAclId(),
									networkAcl
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch vpc list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsNetworkAcl.class, AwsConventionContext.INSTANCE );
	}
}

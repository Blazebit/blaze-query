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
import software.amazon.awssdk.services.ec2.model.DescribeLaunchTemplateVersionsRequest;
import software.amazon.awssdk.services.ec2.model.LaunchTemplateVersion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class AwsEc2LaunchTemplateVersionDataFetcher implements DataFetcher<AwsEc2LaunchTemplateVersion>, Serializable {

	public static final AwsEc2LaunchTemplateVersionDataFetcher INSTANCE = new AwsEc2LaunchTemplateVersionDataFetcher();

	private AwsEc2LaunchTemplateVersionDataFetcher() {
	}

	@Override
	public List<AwsEc2LaunchTemplateVersion> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsEc2LaunchTemplateVersion> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				for ( Region region : account.getRegions() ) {
					Ec2ClientBuilder ec2ClientBuilder = Ec2Client.builder()
							.region( region )
							.credentialsProvider( account.getCredentialsProvider() );
					if ( sdkHttpClient != null ) {
						ec2ClientBuilder.httpClient( sdkHttpClient );
					}
					try (Ec2Client client = ec2ClientBuilder.build()) {
						for ( LaunchTemplateVersion launchTemplateVersion : client.describeLaunchTemplateVersions(
								DescribeLaunchTemplateVersionsRequest.builder()
										.versions( "$Default" )
										.build() ).launchTemplateVersions() ) {
							list.add( new AwsEc2LaunchTemplateVersion(
									account.getAccountId(),
									region.id(),
									launchTemplateVersion.launchTemplateId(),
									launchTemplateVersion
							) );
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch launch template version list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsEc2LaunchTemplateVersion.class, AwsConventionContext.INSTANCE );
	}
}

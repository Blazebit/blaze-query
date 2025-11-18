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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.IamClientBuilder;
import software.amazon.awssdk.services.iam.model.ListMfaDevicesRequest;
import software.amazon.awssdk.services.iam.model.MFADevice;
import software.amazon.awssdk.services.iam.model.NoSuchEntityException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martijn Sprengers
 * @since 1.0.0
 */
public class MFADeviceDataFetcher implements DataFetcher<AwsIamMfaDevice>, Serializable {

	public static final MFADeviceDataFetcher INSTANCE = new MFADeviceDataFetcher();
	private static final Logger log = LoggerFactory.getLogger( MFADeviceDataFetcher.class );

	private MFADeviceDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsIamMfaDevice.class, AwsConventionContext.INSTANCE );
	}

	@Override
	public List<AwsIamMfaDevice> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<? extends AwsIamUser> users = context.getSession().getOrFetch( AwsIamUser.class );
			List<AwsIamMfaDevice> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					for ( AwsIamUser user : users ) {
						if ( user.getAccountId().equals( account.getAccountId() ) ) {
							try {
								ListMfaDevicesRequest request = ListMfaDevicesRequest.builder()
										.userName( user.getPayload().userName() )
										.build();
								for ( MFADevice mfaDevice : client.listMFADevices( request ).mfaDevices() ) {
									list.add( new AwsIamMfaDevice(
											account.getAccountId(),
											null,
											mfaDevice
									) );
								}
							}
							catch (NoSuchEntityException e) {
								// If the user cannot be found, log and continue
								log.warn( "User '{}' cannot be found: {}", user.getPayload().userName(),
										e.getMessage() );
							}
						}
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch MFA devices list", e );
		}
	}
}

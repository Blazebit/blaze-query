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
import software.amazon.awssdk.services.iam.model.ListVirtualMfaDevicesRequest;
import software.amazon.awssdk.services.iam.model.VirtualMFADevice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donghwi Kim
 * @since 1.0.0
 */
public class VirtualMfaDeviceDataFetcher implements DataFetcher<AwsIamVirtualMfaDevice>, Serializable {

	public static final VirtualMfaDeviceDataFetcher INSTANCE = new VirtualMfaDeviceDataFetcher();

	private VirtualMfaDeviceDataFetcher() {
	}

	@Override
	public List<AwsIamVirtualMfaDevice> fetch(DataFetchContext context) {
		try {
			List<AwsConnectorConfig.Account> accounts = AwsConnectorConfig.ACCOUNT.getAll( context );
			SdkHttpClient sdkHttpClient = AwsConnectorConfig.HTTP_CLIENT.find( context );
			List<AwsIamVirtualMfaDevice> list = new ArrayList<>();
			for ( AwsConnectorConfig.Account account : accounts ) {
				IamClientBuilder iamClientBuilder = IamClient.builder()
						// Any region is fine for IAM operations
						.region( account.getRegions().iterator().next() )
						.credentialsProvider( account.getCredentialsProvider() );
				if ( sdkHttpClient != null ) {
					iamClientBuilder.httpClient( sdkHttpClient );
				}
				try (IamClient client = iamClientBuilder.build()) {
					ListVirtualMfaDevicesRequest request = ListVirtualMfaDevicesRequest.builder().build();
					for ( VirtualMFADevice virtualMFADevice : client.listVirtualMFADevicesPaginator( request )
							.virtualMFADevices() ) {
						list.add( new AwsIamVirtualMfaDevice(
								account.getAccountId(),
								virtualMFADevice.serialNumber(),
								virtualMFADevice
						) );
					}
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch virtual MFA devices list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AwsIamVirtualMfaDevice.class, AwsConventionContext.INSTANCE );
	}
}

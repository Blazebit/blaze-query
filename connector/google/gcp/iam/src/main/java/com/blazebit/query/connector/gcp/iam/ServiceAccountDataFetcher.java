/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.iam;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.gcp.base.GcpConnectorConfig;
import com.blazebit.query.connector.gcp.base.GcpConventionContext;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.cloud.iam.admin.v1.IAMClient;
import com.google.cloud.iam.admin.v1.IAMSettings;
import com.google.cloud.resourcemanager.v3.Project;
import com.google.iam.admin.v1.ListServiceAccountsRequest;
import com.google.iam.admin.v1.ServiceAccount;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ServiceAccountDataFetcher implements DataFetcher<ServiceAccount>, Serializable {

	public static final ServiceAccountDataFetcher INSTANCE = new ServiceAccountDataFetcher();

	private ServiceAccountDataFetcher() {
	}

	@Override
	public List<ServiceAccount> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<ServiceAccount> list = new ArrayList<>();
			List<? extends Project> projects = context.getSession().getOrFetch( Project.class );
			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final IAMSettings settings = IAMSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build();
				try (IAMClient client = IAMClient.create( settings )) {
					try {
						for ( Project project : projects ) {
							final ListServiceAccountsRequest request = ListServiceAccountsRequest.newBuilder()
									.setName( project.getName() )
									.build();
							final IAMClient.ListServiceAccountsPagedResponse response = client.listServiceAccounts(
									request );
							for ( ServiceAccount instance : response.iterateAll() ) {
								list.add( instance );
							}
						}
					}
					catch (PermissionDeniedException e) {
						if ( "SERVICE_DISABLED".equals( e.getErrorDetails().getErrorInfo().getReason() ) ) {
							// Ignore this exception, since there are no resources
							continue;
						}
						throw e;
					}
				}
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch service account list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( ServiceAccount.class, GcpConventionContext.INSTANCE );
	}
}

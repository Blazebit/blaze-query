/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.iam;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.gcp.base.GcpConnectorConfig;
import com.blazebit.query.connector.gcp.base.GcpConventionContext;
import com.blazebit.query.connector.gcp.base.GcpProject;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.cloud.iam.admin.v1.IAMClient;
import com.google.cloud.iam.admin.v1.IAMSettings;
import com.google.iam.admin.v1.ListServiceAccountsRequest;
import com.google.iam.admin.v1.ServiceAccount;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ServiceAccountDataFetcher implements DataFetcher<GcpServiceAccount>, Serializable {

	private static final Logger LOG = Logger.getLogger( ServiceAccountDataFetcher.class.getName() );

	public static final ServiceAccountDataFetcher INSTANCE = new ServiceAccountDataFetcher();

	private ServiceAccountDataFetcher() {
	}

	@Override
	public List<GcpServiceAccount> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<GcpServiceAccount> list = new ArrayList<>();
			List<? extends GcpProject> projects = context.getSession().getOrFetch( GcpProject.class );
			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final IAMSettings settings = IAMSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build();
				try (IAMClient client = IAMClient.create( settings )) {
					for ( GcpProject project : projects ) {
						try {
							final ListServiceAccountsRequest request = ListServiceAccountsRequest.newBuilder()
									.setName( project.getPayload().getName() )
									.build();
							final IAMClient.ListServiceAccountsPagedResponse response = client.listServiceAccounts(
									request );
							for ( ServiceAccount instance : response.iterateAll() ) {
								list.add( new GcpServiceAccount( instance.getName(), instance ) );
							}
						}
						catch (PermissionDeniedException e) {
							if ( isServiceDisabled( e ) ) {
								LOG.log( Level.WARNING,
										"IAM API is not enabled for project ''{0}'', skipping service account fetch.",
										project.getPayload().getProjectId() );
								continue;
							}
							throw e;
						}
					}
				}
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch service account list", e );
		}
	}

	private static boolean isServiceDisabled(PermissionDeniedException e) {
		var details = e.getErrorDetails();
		if ( details != null && details.getErrorInfo() != null ) {
			return "SERVICE_DISABLED".equals( details.getErrorInfo().getReason() );
		}
		return false;
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GcpServiceAccount.class, GcpConventionContext.INSTANCE );
	}
}

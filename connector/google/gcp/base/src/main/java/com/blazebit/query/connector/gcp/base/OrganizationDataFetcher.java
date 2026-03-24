/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.base;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.cloud.resourcemanager.v3.Organization;
import com.google.cloud.resourcemanager.v3.OrganizationsClient;
import com.google.cloud.resourcemanager.v3.OrganizationsSettings;

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
public class OrganizationDataFetcher implements DataFetcher<GcpOrganization>, Serializable {

	private static final Logger LOG = Logger.getLogger( OrganizationDataFetcher.class.getName() );

	public static final OrganizationDataFetcher INSTANCE = new OrganizationDataFetcher();

	private OrganizationDataFetcher() {
	}

	@Override
	public List<GcpOrganization> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<GcpOrganization> list = new ArrayList<>();

			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final OrganizationsSettings settings = OrganizationsSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build();
				try (OrganizationsClient client = OrganizationsClient.create( settings )) {
					try {
						final OrganizationsClient.SearchOrganizationsPagedResponse response = client.searchOrganizations( "" );
						for ( Organization instance : response.iterateAll() ) {
							list.add( new GcpOrganization( instance.getName(), instance ) );
						}
					}
					catch (PermissionDeniedException e) {
						if ( isServiceDisabled( e ) ) {
							LOG.log( Level.WARNING,
									"Resource Manager API is not enabled, skipping organization fetch." );
							continue;
						}
						throw e;
					}
				}
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch organization list", e );
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
		return DataFormats.beansConvention( GcpOrganization.class, GcpConventionContext.INSTANCE );
	}
}

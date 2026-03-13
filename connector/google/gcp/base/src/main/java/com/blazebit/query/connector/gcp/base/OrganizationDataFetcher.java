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
import com.google.cloud.resourcemanager.v3.Organization;
import com.google.cloud.resourcemanager.v3.OrganizationsClient;
import com.google.cloud.resourcemanager.v3.OrganizationsSettings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class OrganizationDataFetcher implements DataFetcher<GcpOrganization>, Serializable {

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
					final OrganizationsClient.SearchOrganizationsPagedResponse response = client.searchOrganizations( "" );
					for ( Organization instance : response.iterateAll() ) {
						list.add( new GcpOrganization( instance.getName(), instance ) );
					}
				}
			}
			return list;
		}
		catch (Exception e) {
			throw new DataFetcherException( "Could not fetch organization list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GcpOrganization.class, GcpConventionContext.INSTANCE );
	}
}

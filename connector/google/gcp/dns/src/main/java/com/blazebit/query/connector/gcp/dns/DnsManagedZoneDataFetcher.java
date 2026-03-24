/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.dns;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.gcp.base.GcpConnectorConfig;
import com.blazebit.query.connector.gcp.base.GcpConventionContext;
import com.blazebit.query.connector.gcp.base.GcpProject;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dns.Dns;
import com.google.cloud.dns.DnsOptions;
import com.google.cloud.dns.Zone;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches Cloud DNS managed zones for GCP projects.
 *
 * @author Martijn Sprengers
 * @since 2.3.0
 */
public class DnsManagedZoneDataFetcher implements DataFetcher<GcpDnsManagedZone>, Serializable {

	public static final DnsManagedZoneDataFetcher INSTANCE = new DnsManagedZoneDataFetcher();

	private DnsManagedZoneDataFetcher() {
	}

	@Override
	public List<GcpDnsManagedZone> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<GcpDnsManagedZone> list = new ArrayList<>();
			List<? extends GcpProject> projects = context.getSession().getOrFetch( GcpProject.class );
			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				GoogleCredentials credentials = (GoogleCredentials) credentialsProvider.getCredentials();
				for ( GcpProject project : projects ) {
					try {
						Dns dns = DnsOptions.newBuilder()
								.setCredentials( credentials )
								.setProjectId( project.getPayload().getProjectId() )
								.build()
								.getService();
						for ( Zone zone : dns.listZones().iterateAll() ) {
							list.add( new GcpDnsManagedZone( zone.getName(), zone ) );
						}
					}
					catch (com.google.cloud.dns.DnsException e) {
						if ( e.getCode() == 403 ) {
							// Ignore this exception, since there are no resources or access is denied
							continue;
						}
						throw e;
					}
				}
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch Cloud DNS managed zone list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GcpDnsManagedZone.class, GcpConventionContext.INSTANCE );
	}
}

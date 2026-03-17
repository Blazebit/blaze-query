/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.compute;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.gcp.base.GcpConnectorConfig;
import com.blazebit.query.connector.gcp.base.GcpConventionContext;
import com.blazebit.query.connector.gcp.base.GcpProject;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.client.http.HttpResponseException;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.cloud.compute.v1.Firewall;
import com.google.cloud.compute.v1.FirewallsClient;
import com.google.cloud.compute.v1.FirewallsSettings;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches VPC Firewall rules for GCP projects.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class FirewallRuleDataFetcher implements DataFetcher<GcpFirewallRule>, Serializable {

	public static final FirewallRuleDataFetcher INSTANCE = new FirewallRuleDataFetcher();

	private FirewallRuleDataFetcher() {
	}

	@Override
	public List<GcpFirewallRule> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<GcpFirewallRule> list = new ArrayList<>();
			List<? extends GcpProject> projects = context.getSession().getOrFetch( GcpProject.class );
			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final FirewallsSettings settings = FirewallsSettings.newBuilder()
						.setCredentialsProvider( credentialsProvider )
						.build();
				try (FirewallsClient client = FirewallsClient.create( settings )) {
					for ( GcpProject project : projects ) {
						try {
							for ( Firewall firewall : client.list( project.getPayload().getProjectId() ).iterateAll() ) {
								list.add( new GcpFirewallRule( firewall.getName(), firewall ) );
							}
						}
						catch (PermissionDeniedException e) {
							if ( e.getCause() instanceof HttpResponseException
									&& ((HttpResponseException) e.getCause()).getContent()
									.contains( "\"accessNotConfigured\"" ) ) {
								// Ignore this exception, since there are no resources
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
			throw new DataFetcherException( "Could not fetch firewall rule list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GcpFirewallRule.class, GcpConventionContext.INSTANCE );
	}
}

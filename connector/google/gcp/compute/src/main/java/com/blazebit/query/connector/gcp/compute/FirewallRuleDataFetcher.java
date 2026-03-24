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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fetches VPC Firewall rules for GCP projects.
 *
 * @author Martijn Sprengers
 * @since 2.3.0
 */
public class FirewallRuleDataFetcher implements DataFetcher<GcpFirewallRule>, Serializable {

	private static final Logger LOG = Logger.getLogger( FirewallRuleDataFetcher.class.getName() );

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
							if ( e.getCause() instanceof HttpResponseException httpEx
									&& httpEx.getContent() != null
									&& httpEx.getContent().contains( "\"accessNotConfigured\"" ) ) {
								// The Compute Engine API is not enabled for this project.
								// No firewall rules exist, so skip it and warn the user.
								LOG.log( Level.WARNING,
										"Compute Engine API is not enabled for project ''{0}'', skipping firewall rule fetch.",
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
			throw new DataFetcherException( "Could not fetch firewall rule list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GcpFirewallRule.class, GcpConventionContext.INSTANCE );
	}
}

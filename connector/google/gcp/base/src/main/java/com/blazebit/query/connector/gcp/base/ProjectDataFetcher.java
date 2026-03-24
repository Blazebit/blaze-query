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
import com.google.cloud.resourcemanager.v3.ListProjectsRequest;
import com.google.cloud.resourcemanager.v3.Project;
import com.google.cloud.resourcemanager.v3.ProjectsClient;
import com.google.cloud.resourcemanager.v3.ProjectsSettings;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ProjectDataFetcher implements DataFetcher<GcpProject>, Serializable {

	private static final Logger LOG = Logger.getLogger( ProjectDataFetcher.class.getName() );

	public static final ProjectDataFetcher INSTANCE = new ProjectDataFetcher();

	private ProjectDataFetcher() {
	}

	@Override
	public List<GcpProject> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			// Use LinkedHashMap to deduplicate projects by name while preserving insertion order.
			// searchProjects("") already returns all visible projects, but iterating org/folder
			// parents can surface the same projects again.
			Map<String, GcpProject> seen = new LinkedHashMap<>();
			List<? extends GcpOrganization> organizations = context.getSession().getOrFetch( GcpOrganization.class );
			List<? extends GcpFolder> folders = context.getSession().getOrFetch( GcpFolder.class );

			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final ProjectsSettings settings = ProjectsSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build();
				try (ProjectsClient client = ProjectsClient.create( settings )) {
					try {
						ProjectsClient.SearchProjectsPagedResponse searchProjectsPagedResponse =
								client.searchProjects( "" );

						for ( Project project : searchProjectsPagedResponse.iterateAll() ) {
							seen.putIfAbsent( project.getName(), new GcpProject( project.getName(), project ) );
						}
					}
					catch (PermissionDeniedException e) {
						if ( isServiceDisabled( e ) ) {
							LOG.log( Level.WARNING,
									"Resource Manager API is not enabled, skipping project search." );
						}
						else {
							throw e;
						}
					}
					for ( GcpOrganization organization : organizations ) {
						try {
							final ListProjectsRequest request = ListProjectsRequest.newBuilder()
									.setParent( organization.getPayload().getName() )
									.build();
							final ProjectsClient.ListProjectsPagedResponse response = client.listProjects( request );
							for ( Project instance : response.iterateAll() ) {
								seen.putIfAbsent( instance.getName(), new GcpProject( instance.getName(), instance ) );
							}
						}
						catch (PermissionDeniedException e) {
							if ( isServiceDisabled( e ) ) {
								LOG.log( Level.WARNING,
										"Resource Manager API is not enabled for organization ''{0}'', skipping project listing.",
										organization.getPayload().getName() );
								continue;
							}
							throw e;
						}
					}
					for ( GcpFolder folder : folders ) {
						try {
							final ListProjectsRequest request = ListProjectsRequest.newBuilder()
									.setParent( folder.getPayload().getName() )
									.build();
							final ProjectsClient.ListProjectsPagedResponse response = client.listProjects( request );
							for ( Project instance : response.iterateAll() ) {
								seen.putIfAbsent( instance.getName(), new GcpProject( instance.getName(), instance ) );
							}
						}
						catch (PermissionDeniedException e) {
							if ( isServiceDisabled( e ) ) {
								LOG.log( Level.WARNING,
										"Resource Manager API is not enabled for folder ''{0}'', skipping project listing.",
										folder.getPayload().getName() );
								continue;
							}
							throw e;
						}
					}
				}
			}
			return new ArrayList<>( seen.values() );
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch project list", e );
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
		return DataFormats.beansConvention( GcpProject.class, GcpConventionContext.INSTANCE );
	}
}

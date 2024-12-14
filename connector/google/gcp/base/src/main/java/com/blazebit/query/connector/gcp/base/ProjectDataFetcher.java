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
import com.google.cloud.resourcemanager.v3.Folder;
import com.google.cloud.resourcemanager.v3.ListProjectsRequest;
import com.google.cloud.resourcemanager.v3.Organization;
import com.google.cloud.resourcemanager.v3.Project;
import com.google.cloud.resourcemanager.v3.ProjectsClient;
import com.google.cloud.resourcemanager.v3.ProjectsSettings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class ProjectDataFetcher implements DataFetcher<Project>, Serializable {

	public static final ProjectDataFetcher INSTANCE = new ProjectDataFetcher();

	private ProjectDataFetcher() {
	}

	@Override
	public List<Project> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<Project> list = new ArrayList<>();
			List<? extends Organization> organizations = context.getSession().getOrFetch( Organization.class );
			List<? extends Folder> folders = context.getSession().getOrFetch( Folder.class );

			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final ProjectsSettings settings = ProjectsSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build();
				try (ProjectsClient client = ProjectsClient.create( settings )) {
					for ( Organization organization : organizations ) {
						final ListProjectsRequest request = ListProjectsRequest.newBuilder()
								.setParent( organization.getName() )
								.build();
						final ProjectsClient.ListProjectsPagedResponse response = client.listProjects( request );
						for ( Project instance : response.iterateAll() ) {
							list.add( instance );
						}
					}
					for ( Folder folder : folders ) {
						final ListProjectsRequest request = ListProjectsRequest.newBuilder()
								.setParent( folder.getName() )
								.build();
						final ProjectsClient.ListProjectsPagedResponse response = client.listProjects( request );
						for ( Project instance : response.iterateAll() ) {
							list.add( instance );
						}
					}
				}
			}
			return list;
		}
		catch (Exception e) {
			throw new DataFetcherException( "Could not fetch project list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( Project.class, GcpConventionContext.INSTANCE );
	}
}

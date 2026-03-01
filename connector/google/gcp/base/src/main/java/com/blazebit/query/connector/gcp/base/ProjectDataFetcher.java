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
import com.google.cloud.resourcemanager.v3.ListProjectsRequest;
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
public class ProjectDataFetcher implements DataFetcher<GcpProject>, Serializable {

	public static final ProjectDataFetcher INSTANCE = new ProjectDataFetcher();

	private ProjectDataFetcher() {
	}

	@Override
	public List<GcpProject> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<GcpProject> list = new ArrayList<>();
			List<? extends GcpOrganization> organizations = context.getSession().getOrFetch( GcpOrganization.class );
			List<? extends GcpFolder> folders = context.getSession().getOrFetch( GcpFolder.class );

			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final ProjectsSettings settings = ProjectsSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build();
				try (ProjectsClient client = ProjectsClient.create( settings )) {
					ProjectsClient.SearchProjectsPagedResponse searchProjectsPagedResponse =
							client.searchProjects("");

					for (Project project : searchProjectsPagedResponse.iterateAll()) {
						list.add( new GcpProject( project.getName(), project ) );
					}
					for ( GcpOrganization organization : organizations ) {
						final ListProjectsRequest request = ListProjectsRequest.newBuilder()
								.setParent( organization.getPayload().getName() )
								.build();
						final ProjectsClient.ListProjectsPagedResponse response = client.listProjects( request );
						for ( Project instance : response.iterateAll() ) {
							list.add( new GcpProject( instance.getName(), instance ) );
						}
					}
					for ( GcpFolder folder : folders ) {
						final ListProjectsRequest request = ListProjectsRequest.newBuilder()
								.setParent( folder.getPayload().getName() )
								.build();
						final ProjectsClient.ListProjectsPagedResponse response = client.listProjects( request );
						for ( Project instance : response.iterateAll() ) {
							list.add( new GcpProject( instance.getName(), instance ) );
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
		return DataFormats.beansConvention( GcpProject.class, GcpConventionContext.INSTANCE );
	}
}

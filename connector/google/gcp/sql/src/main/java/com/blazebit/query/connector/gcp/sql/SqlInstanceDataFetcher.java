/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.sql;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.gcp.base.GcpConnectorConfig;
import com.blazebit.query.connector.gcp.base.GcpProject;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.services.sqladmin.SQLAdmin;
import com.google.api.services.sqladmin.model.DatabaseInstance;
import com.google.api.services.sqladmin.model.InstancesListResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches Cloud SQL database instances for GCP projects.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class SqlInstanceDataFetcher implements DataFetcher<GcpSqlInstance>, Serializable {

	public static final SqlInstanceDataFetcher INSTANCE = new SqlInstanceDataFetcher();

	private static final String APPLICATION_NAME = "blaze-query";

	private SqlInstanceDataFetcher() {
	}

	@Override
	public List<GcpSqlInstance> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<GcpSqlInstance> list = new ArrayList<>();
			List<? extends GcpProject> projects = context.getSession().getOrFetch( GcpProject.class );
			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				GoogleCredentials credentials = (GoogleCredentials) credentialsProvider.getCredentials();
				SQLAdmin sqladmin = new SQLAdmin.Builder(
						GoogleNetHttpTransport.newTrustedTransport(),
						GsonFactory.getDefaultInstance(),
						new HttpCredentialsAdapter( credentials ) )
						.setApplicationName( APPLICATION_NAME )
						.build();
				for ( GcpProject project : projects ) {
					try {
						String projectId = project.getPayload().getProjectId();
						SQLAdmin.Instances.List request = sqladmin.instances().list( projectId );
						InstancesListResponse response;
						do {
							response = request.execute();
							if ( response.getItems() != null ) {
								for ( DatabaseInstance instance : response.getItems() ) {
									list.add( new GcpSqlInstance( instance.getName(), instance ) );
								}
							}
							request.setPageToken( response.getNextPageToken() );
						}
						while ( response.getNextPageToken() != null );
					}
					catch (GoogleJsonResponseException e) {
						if ( e.getStatusCode() == 403 && e.getDetails() != null
								&& e.getDetails().getErrors() != null
								&& e.getDetails().getErrors().stream()
								.anyMatch( err -> "accessNotConfigured".equals( err.getReason() ) ) ) {
							// Ignore this exception, since there are no resources
							continue;
						}
						throw e;
					}
				}
			}
			return list;
		}
		catch (IOException | GeneralSecurityException e) {
			throw new DataFetcherException( "Could not fetch Cloud SQL instance list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GcpSqlInstance.class, GcpSqlConventionContext.INSTANCE );
	}
}

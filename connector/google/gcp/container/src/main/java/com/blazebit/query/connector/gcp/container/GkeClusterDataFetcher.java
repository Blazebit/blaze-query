/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.container;

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
import com.google.cloud.container.v1.ClusterManagerClient;
import com.google.cloud.container.v1.ClusterManagerSettings;
import com.google.container.v1.Cluster;
import com.google.container.v1.ListClustersRequest;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches GKE clusters for GCP projects.
 *
 * @author Martijn Sprengers
 * @since 2.3.0
 */
public class GkeClusterDataFetcher implements DataFetcher<GcpGkeCluster>, Serializable {

	public static final GkeClusterDataFetcher INSTANCE = new GkeClusterDataFetcher();

	private GkeClusterDataFetcher() {
	}

	@Override
	public List<GcpGkeCluster> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<GcpGkeCluster> list = new ArrayList<>();
			List<? extends GcpProject> projects = context.getSession().getOrFetch( GcpProject.class );
			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final ClusterManagerSettings settings = ClusterManagerSettings.newBuilder()
						.setCredentialsProvider( credentialsProvider )
						.build();
				try (ClusterManagerClient client = ClusterManagerClient.create( settings )) {
					for ( GcpProject project : projects ) {
						try {
							ListClustersRequest request = ListClustersRequest.newBuilder()
									.setParent( "projects/" + project.getPayload().getProjectId() + "/locations/-" )
									.build();
							for ( Cluster cluster : client.listClusters( request ).getClustersList() ) {
								list.add( new GcpGkeCluster( cluster.getName(), cluster ) );
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
			throw new DataFetcherException( "Could not fetch GKE cluster list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GcpGkeCluster.class, GcpConventionContext.INSTANCE );
	}
}

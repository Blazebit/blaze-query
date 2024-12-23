/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.compute;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.gcp.base.GcpConnectorConfig;
import com.blazebit.query.connector.gcp.base.GcpConventionContext;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.client.http.HttpResponseException;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.compute.v1.InstancesScopedList;
import com.google.cloud.compute.v1.InstancesSettings;
import com.google.cloud.resourcemanager.v3.Project;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class InstanceDataFetcher implements DataFetcher<Instance>, Serializable {

	public static final InstanceDataFetcher INSTANCE = new InstanceDataFetcher();

	private InstanceDataFetcher() {
	}

	@Override
	public List<Instance> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<Instance> list = new ArrayList<>();
			List<? extends Project> projects = context.getSession().getOrFetch( Project.class );
			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final InstancesSettings settings = InstancesSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build();
				try (InstancesClient client = InstancesClient.create( settings )) {
					for ( Project project : projects ) {
						try {
							final InstancesClient.AggregatedListPagedResponse response = client.aggregatedList( project.getProjectId() );
							for ( Map.Entry<String, InstancesScopedList> entry : response.iterateAll() ) {
								list.addAll( entry.getValue().getInstancesList() );
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
			throw new DataFetcherException( "Could not fetch instance list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( Instance.class, GcpConventionContext.INSTANCE );
	}
}

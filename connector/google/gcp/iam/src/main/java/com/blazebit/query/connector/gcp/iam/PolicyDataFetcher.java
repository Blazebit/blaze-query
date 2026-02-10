/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.iam;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.gcp.base.GcpConnectorConfig;
import com.blazebit.query.connector.gcp.base.GcpConventionContext;
import com.blazebit.query.connector.gcp.base.GcpOrganization;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.cloud.asset.v1.AssetServiceClient;
import com.google.cloud.asset.v1.AssetServiceSettings;
import com.google.cloud.asset.v1.IamPolicySearchResult;
import com.google.cloud.asset.v1.SearchAllIamPoliciesRequest;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class PolicyDataFetcher implements DataFetcher<GcpIamPolicy>, Serializable {

	public static final PolicyDataFetcher INSTANCE = new PolicyDataFetcher();

	private PolicyDataFetcher() {
	}

	@Override
	public List<GcpIamPolicy> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<GcpIamPolicy> list = new ArrayList<>();
			List<? extends GcpOrganization> organizations = context.getSession().getOrFetch( GcpOrganization.class );
			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final AssetServiceSettings settings = AssetServiceSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build();
				try (AssetServiceClient client = AssetServiceClient.create( settings )) {
					try {
						for ( GcpOrganization organization : organizations ) {
							final SearchAllIamPoliciesRequest request = SearchAllIamPoliciesRequest.newBuilder()
									.setScope( organization.getPayload().getName() )
									.build();
							final AssetServiceClient.SearchAllIamPoliciesPagedResponse response = client.searchAllIamPolicies( request );
							for ( IamPolicySearchResult instance : response.iterateAll() ) {
								list.add( new GcpIamPolicy( instance.getResource(), instance ) );
							}
						}
					}
					catch (PermissionDeniedException e) {
						if ( "SERVICE_DISABLED".equals( e.getErrorDetails().getErrorInfo().getReason() ) ) {
							// Ignore this exception, since there are no resources
							continue;
						}
						throw e;
					}
				}
			}
			return list;
		}
		catch (IOException e) {
			throw new DataFetcherException( "Could not fetch policy list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GcpIamPolicy.class, GcpConventionContext.INSTANCE );
	}
}

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
import com.google.rpc.ErrorInfo;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class PolicyDataFetcher implements DataFetcher<GcpIamPolicy>, Serializable {

	private static final Logger LOG = Logger.getLogger( PolicyDataFetcher.class.getName() );

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
					for ( GcpOrganization organization : organizations ) {
						try {
							final SearchAllIamPoliciesRequest request = SearchAllIamPoliciesRequest.newBuilder()
									.setScope( organization.getPayload().getName() )
									.build();
							final AssetServiceClient.SearchAllIamPoliciesPagedResponse response = client.searchAllIamPolicies( request );
							for ( IamPolicySearchResult instance : response.iterateAll() ) {
								list.add( new GcpIamPolicy( instance.getResource(), instance ) );
							}
						}
						catch (PermissionDeniedException e) {
							if ( isServiceDisabled( e ) ) {
								LOG.log( Level.WARNING,
										"Cloud Asset API is not enabled, skipping IAM policy fetch for organization ''{0}''.",
										organization.getPayload().getName() );
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
			throw new DataFetcherException( "Could not fetch policy list", e );
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
		return DataFormats.beansConvention( GcpIamPolicy.class, GcpConventionContext.INSTANCE );
	}
}

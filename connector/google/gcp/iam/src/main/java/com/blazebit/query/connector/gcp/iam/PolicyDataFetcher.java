/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.iam;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.connector.gcp.base.GcpConnectorConfig;
import com.blazebit.query.connector.gcp.base.GcpConventionContext;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.iam.v2.ListPoliciesRequest;
import com.google.iam.v2.PoliciesClient;
import com.google.iam.v2.PoliciesSettings;
import com.google.iam.v2.Policy;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class PolicyDataFetcher implements DataFetcher<Policy>, Serializable {

	public static final PolicyDataFetcher INSTANCE = new PolicyDataFetcher();

	private PolicyDataFetcher() {
	}

	@Override
	public List<Policy> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<Policy> list = new ArrayList<>();
			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final PoliciesSettings settings = PoliciesSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build();
				try (PoliciesClient client = PoliciesClient.create( settings )) {
					try {
						final ListPoliciesRequest request = ListPoliciesRequest.newBuilder()
//							.setName("970024905535")
								.build();
						final PoliciesClient.ListPoliciesPagedResponse response = client.listPolicies( request );
						for ( Policy instance : response.iterateAll() ) {
							list.add( instance );
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
		return DataFormats.beansConvention( Policy.class, GcpConventionContext.INSTANCE );
	}
}

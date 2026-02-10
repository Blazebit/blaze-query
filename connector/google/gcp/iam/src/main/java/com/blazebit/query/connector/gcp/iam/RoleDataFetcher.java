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
import com.google.cloud.iam.admin.v1.IAMClient;
import com.google.cloud.iam.admin.v1.IAMSettings;
import com.google.iam.admin.v1.ListRolesRequest;
import com.google.iam.admin.v1.Role;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class RoleDataFetcher implements DataFetcher<GcpRole>, Serializable {

	public static final RoleDataFetcher INSTANCE = new RoleDataFetcher();

	private RoleDataFetcher() {
	}

	@Override
	public List<GcpRole> fetch(DataFetchContext context) {
		try {
			List<CredentialsProvider> credentialsProviders = GcpConnectorConfig.GCP_CREDENTIALS_PROVIDER.getAll( context );
			List<GcpRole> list = new ArrayList<>();
			for ( CredentialsProvider credentialsProvider : credentialsProviders ) {
				final IAMSettings settings = IAMSettings.newBuilder()
						.setCredentialsProvider(credentialsProvider)
						.build();
				try (IAMClient client = IAMClient.create( settings )) {
					try {
						final ListRolesRequest request = ListRolesRequest.newBuilder()
	//							.setName("970024905535")
	//							.setAddress(newAddressName)
	//							.setRegion(REGION)
								.build();
						final IAMClient.ListRolesPagedResponse response = client.listRoles( request );
						for ( Role instance : response.iterateAll() ) {
							list.add( new GcpRole( instance.getName(), instance ) );
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
			throw new DataFetcherException( "Could not fetch role list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( GcpRole.class, GcpConventionContext.INSTANCE );
	}
}

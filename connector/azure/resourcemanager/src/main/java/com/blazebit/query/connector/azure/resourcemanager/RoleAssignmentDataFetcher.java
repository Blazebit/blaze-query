/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.authorization.models.RoleAssignment;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A data fetcher for Azure role assignments via the ARM Authorization API.
 *
 * <p>Requires {@link AzureResourceManagerConnectorConfig#AZURE_RESOURCE_MANAGER} to be configured.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class RoleAssignmentDataFetcher implements DataFetcher<AzureResourceRoleAssignment>, Serializable {

	public static final RoleAssignmentDataFetcher INSTANCE = new RoleAssignmentDataFetcher();

	private RoleAssignmentDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AzureResourceRoleAssignment.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}

	@Override
	public List<AzureResourceRoleAssignment> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManager> resourceManagers =
					AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll( context );
			List<AzureResourceRoleAssignment> list = new ArrayList<>();
			for ( AzureResourceManager resourceManager : resourceManagers ) {
				String tenantId = resourceManager.tenantId();
				String subscriptionId = resourceManager.subscriptionId();
				String subscriptionScope = "/subscriptions/" + subscriptionId;
				for ( RoleAssignment roleAssignment : resourceManager.accessManagement().roleAssignments().listByScope( subscriptionScope ) ) {
					String id = roleAssignment.id();
					String resourceGroupName = parseResourceGroupName( id );
					list.add( new AzureResourceRoleAssignment(
							tenantId,
							subscriptionId,
							resourceGroupName,
							roleAssignment.name(),
							roleAssignment.innerModel() ) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch role assignment list", e );
		}
	}

	private static String parseResourceGroupName(String resourceId) {
		if ( resourceId == null ) {
			return null;
		}
		String[] parts = resourceId.split( "/" );
		// /subscriptions/{sub}/resourceGroups/{rg}/providers/...
		if ( parts.length >= 5 && "resourceGroups".equalsIgnoreCase( parts[3] ) ) {
			return parts[4];
		}
		return null;
	}
}

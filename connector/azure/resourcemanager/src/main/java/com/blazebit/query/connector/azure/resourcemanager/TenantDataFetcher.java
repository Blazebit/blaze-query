/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.resources.models.Tenant;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class TenantDataFetcher implements DataFetcher<AzureResourceManagerTenant>, Serializable {

	public static final TenantDataFetcher INSTANCE = new TenantDataFetcher();

	private TenantDataFetcher() {
	}

	@Override
	public List<AzureResourceManagerTenant> fetch(DataFetchContext context) {
		try {
			List<AzureResourceManager> resourceManagers = AzureResourceManagerConnectorConfig.AZURE_RESOURCE_MANAGER.getAll(
					context );
			List<AzureResourceManagerTenant> list = new ArrayList<>();
			for ( AzureResourceManager resourceManager : resourceManagers ) {
				for ( Tenant tenant : resourceManager.tenants().list() ) {
					list.add( new AzureResourceManagerTenant( tenant.tenantId(), tenant.innerModel() ) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch subscription list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( AzureResourceManagerTenant.class,
				AzureResourceManagerConventionContext.INSTANCE );
	}
}

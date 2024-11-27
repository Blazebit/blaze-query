/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.subscription.v20221201;

import com.blazebit.query.connector.azure.base.invoker.ApiClient;
import com.blazebit.query.spi.DataFetcherException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.blazebit.query.connector.azure.base.AzureConnectorConfig;
import com.blazebit.query.connector.azure.base.invoker.ApiException;
import com.blazebit.query.connector.azure.subscription.v20221201.api.TenantsApi;
import com.blazebit.query.connector.azure.subscription.v20221201.model.TenantIdDescription;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFormat;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class TenantIdDescriptionDataFetcher implements DataFetcher<TenantIdDescription>, Serializable {

	public static final TenantIdDescriptionDataFetcher INSTANCE = new TenantIdDescriptionDataFetcher();

	private TenantIdDescriptionDataFetcher() {
	}

	@Override
	public List<TenantIdDescription> fetch(DataFetchContext context) {
		try {
			List<ApiClient> apiClients = AzureConnectorConfig.API_CLIENT.getAll( context );
			List<TenantIdDescription> list = new ArrayList<>();
			for ( ApiClient apiClient : apiClients ) {
				list.addAll( new TenantsApi( apiClient ).tenantsList( "2022-12-01" ).getValue() );
			}
			return list;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch tenant list", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( TenantIdDescription.class );
	}
}

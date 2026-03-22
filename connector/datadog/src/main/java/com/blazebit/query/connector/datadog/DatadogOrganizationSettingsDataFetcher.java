/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;
import com.datadog.api.client.ApiClient;
import com.datadog.api.client.ApiException;
import com.datadog.api.client.v1.api.OrganizationsApi;
import com.datadog.api.client.v1.model.OrganizationListResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link DatadogOrganizationSettings} from the Datadog Organizations API (v1).
 * Returns security settings per organization including SAML configuration and strict mode.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class DatadogOrganizationSettingsDataFetcher implements DataFetcher<DatadogOrganizationSettings>, Serializable {

	public static final DatadogOrganizationSettingsDataFetcher INSTANCE = new DatadogOrganizationSettingsDataFetcher();

	private DatadogOrganizationSettingsDataFetcher() {
	}

	@Override
	public List<DatadogOrganizationSettings> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			List<DatadogOrganizationSettings> result = new ArrayList<>();
			for ( ApiClient client : clients ) {
				OrganizationsApi api = new OrganizationsApi( client );
				OrganizationListResponse response = api.listOrgs();
				if ( response.getOrgs() != null ) {
					response.getOrgs().stream()
							.map( DatadogOrganizationSettings::from )
							.forEach( result::add );
				}
			}
			return result;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch Datadog organization settings", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogOrganizationSettings.class, DatadogConventionContext.INSTANCE );
	}
}

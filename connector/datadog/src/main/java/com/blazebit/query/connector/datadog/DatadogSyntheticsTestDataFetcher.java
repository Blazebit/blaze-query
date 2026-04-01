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
import com.datadog.api.client.v1.api.SyntheticsApi;
import com.datadog.api.client.v1.model.SyntheticsListTestsResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches {@link DatadogSyntheticsTest} entries from the Datadog Synthetics API (v1).
 * Returns all configured synthetic tests including API tests, browser tests, and mobile tests.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
 */
public class DatadogSyntheticsTestDataFetcher implements DataFetcher<DatadogSyntheticsTest>, Serializable {

	public static final DatadogSyntheticsTestDataFetcher INSTANCE = new DatadogSyntheticsTestDataFetcher();

	private DatadogSyntheticsTestDataFetcher() {
	}

	@Override
	public List<DatadogSyntheticsTest> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			List<DatadogSyntheticsTest> result = new ArrayList<>();
			for ( ApiClient client : clients ) {
				SyntheticsApi api = new SyntheticsApi( client );
				SyntheticsListTestsResponse response = api.listTests();
				if ( response.getTests() != null ) {
					response.getTests().stream()
							.map( DatadogSyntheticsTest::from )
							.forEach( result::add );
				}
			}
			return result;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch Datadog synthetics tests", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogSyntheticsTest.class, DatadogConventionContext.INSTANCE );
	}
}

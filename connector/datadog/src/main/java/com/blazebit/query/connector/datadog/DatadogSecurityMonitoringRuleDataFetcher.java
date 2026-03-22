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
import com.datadog.api.client.v2.api.SecurityMonitoringApi;
import com.datadog.api.client.v2.api.SecurityMonitoringApi.ListSecurityMonitoringRulesOptionalParameters;
import com.datadog.api.client.v2.model.SecurityMonitoringListRulesResponse;
import com.datadog.api.client.v2.model.SecurityMonitoringRuleResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fetches {@link DatadogSecurityMonitoringRule} entries from the Datadog Security Monitoring API (v2).
 * Detection rules cover log detection, CSPM, workload security, and infrastructure configuration.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class DatadogSecurityMonitoringRuleDataFetcher implements DataFetcher<DatadogSecurityMonitoringRule>, Serializable {

	public static final DatadogSecurityMonitoringRuleDataFetcher INSTANCE = new DatadogSecurityMonitoringRuleDataFetcher();

	private static final long PAGE_SIZE = 100L;

	private DatadogSecurityMonitoringRuleDataFetcher() {
	}

	@Override
	public List<DatadogSecurityMonitoringRule> fetch(DataFetchContext context) {
		try {
			List<ApiClient> clients = DatadogConnectorConfig.DATADOG_API_CLIENT.getAll( context );
			List<DatadogSecurityMonitoringRule> result = new ArrayList<>();
			for ( ApiClient client : clients ) {
				SecurityMonitoringApi api = new SecurityMonitoringApi( client );
				long pageNumber = 0;
				List<SecurityMonitoringRuleResponse> batch;
				do {
					SecurityMonitoringListRulesResponse response = api.listSecurityMonitoringRules(
							new ListSecurityMonitoringRulesOptionalParameters()
									.pageSize( PAGE_SIZE )
									.pageNumber( pageNumber ) );
					batch = response.getData();
					if ( batch != null ) {
						batch.stream()
								.map( DatadogSecurityMonitoringRule::from )
								.filter( Objects::nonNull )
								.forEach( result::add );
					}
					pageNumber++;
				} while ( batch != null && batch.size() == PAGE_SIZE );
			}
			return result;
		}
		catch (ApiException e) {
			throw new DataFetcherException( "Could not fetch Datadog security monitoring rules", e );
		}
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.componentMethodConvention( DatadogSecurityMonitoringRule.class, DatadogConventionContext.INSTANCE );
	}
}

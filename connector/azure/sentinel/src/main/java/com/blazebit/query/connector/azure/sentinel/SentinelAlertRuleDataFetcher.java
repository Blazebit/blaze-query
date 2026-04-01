/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.sentinel;

import com.azure.resourcemanager.securityinsights.models.AlertRule;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFetcherException;
import com.blazebit.query.spi.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A data fetcher for fetching Microsoft Sentinel alert rules.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class SentinelAlertRuleDataFetcher implements DataFetcher<SentinelAlertRule>, Serializable {

	public static final SentinelAlertRuleDataFetcher INSTANCE = new SentinelAlertRuleDataFetcher();

	private SentinelAlertRuleDataFetcher() {
	}

	@Override
	public DataFormat getDataFormat() {
		return DataFormats.beansConvention( SentinelAlertRule.class, SentinelConventionContext.INSTANCE );
	}

	@Override
	public List<SentinelAlertRule> fetch(DataFetchContext context) {
		try {
			List<SentinelClientAccessor> accessors = SentinelConnectorConfig.SENTINEL_CLIENT.getAll( context );
			List<SentinelAlertRule> list = new ArrayList<>();
			for ( SentinelClientAccessor accessor : accessors ) {
				for ( AlertRule alertRule : accessor.getManager().alertRules()
						.list( accessor.getResourceGroupName(), accessor.getWorkspaceName() ) ) {
					list.add( new SentinelAlertRule(
							accessor.getTenantId(),
							accessor.getSubscriptionId(),
							accessor.getResourceGroupName(),
							accessor.getWorkspaceName(),
							alertRule.innerModel() ) );
				}
			}
			return list;
		}
		catch (RuntimeException e) {
			throw new DataFetcherException( "Could not fetch Sentinel alert rules", e );
		}
	}
}

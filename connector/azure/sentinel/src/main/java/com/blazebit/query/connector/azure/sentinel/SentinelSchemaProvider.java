/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.sentinel;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the Microsoft Sentinel connector.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public final class SentinelSchemaProvider implements QuerySchemaProvider {

	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				SentinelIncidentDataFetcher.INSTANCE,
				SentinelAlertRuleDataFetcher.INSTANCE,
				SentinelDataConnectorDataFetcher.INSTANCE );
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.linear;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * Registers all Linear data fetchers.
 *
 * @author Martijn Sprengers
 * @since 2.4.4
 */
public final class LinearSchemaProvider implements QuerySchemaProvider {

	public LinearSchemaProvider() {
	}

	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				LinearIssueDataFetcher.INSTANCE,
				LinearUserDataFetcher.INSTANCE,
				LinearTeamDataFetcher.INSTANCE,
				LinearWorkflowStateDataFetcher.INSTANCE,
				LinearIssueLabelDataFetcher.INSTANCE,
				LinearProjectDataFetcher.INSTANCE,
				LinearCycleDataFetcher.INSTANCE
		);
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.datacenter;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the Jira Data center connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class JiraDatacenterSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public JiraDatacenterSchemaProvider() {
	}

	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				UserDataFetcher.INSTANCE,
				GroupDataFetcher.INSTANCE,
				MemberDataFetcher.INSTANCE,
				PermissionDataFetcher.INSTANCE,
				ProjectDataFetcher.INSTANCE
		);
	}
}

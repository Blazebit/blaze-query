/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the Jira Cloud connector.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class JiraCloudSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public JiraCloudSchemaProvider() {
	}

	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				UserDataFetcher.INSTANCE,
				PermissionDataFetcher.INSTANCE,
				ProjectDataFetcher.INSTANCE,
				GroupDataFetcher.INSTANCE,
				MemberDataFetcher.INSTANCE
		);
	}
}

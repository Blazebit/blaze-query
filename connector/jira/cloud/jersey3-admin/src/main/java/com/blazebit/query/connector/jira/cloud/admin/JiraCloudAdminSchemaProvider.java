/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.jira.cloud.admin;

import com.blazebit.query.spi.ConfigurationProvider;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.QuerySchemaProvider;

import java.util.Set;

/**
 * The schema provider for the Jira Cloud User Management connector.
 *
 * @author Dimitar Prisadnikov
 * @since 1.0.x
 */
public final class JiraCloudAdminSchemaProvider implements QuerySchemaProvider {
	/**
	 * Creates a new schema provider.
	 */
	public JiraCloudAdminSchemaProvider() {
	}

	@Override
	public Set<? extends DataFetcher<?>> resolveSchemaObjects(ConfigurationProvider configurationProvider) {
		return Set.of(
				JiraCloudAdminOrgDataFetcher.INSTANCE,
				JiraCloudAdminDirectoryDataFetcher.INSTANCE,
				JiraCloudAdminUserDataFetcher.INSTANCE
		);
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.postgresqlflexibleserver.PostgreSqlManager;

/**
 * @author Martijn Sprengers
 * @since 1.0.3
 */
public class AzureResourceManagerPostgreSqlManager extends AzureResourceManagerWrapper<PostgreSqlManager> {
	public AzureResourceManagerPostgreSql(String tenantId, String subscriptionId, PostgreSqlManager payload) {
		super( tenantId, subscriptionId, payload );
	}

	@Override
	public PostgreSqlManager getManager() {
		return super.getManager();
	}
}

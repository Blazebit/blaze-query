/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.postgresqlflexibleserver.PostgreSqlManager;

public class AzureResourceManagerPostgreSql extends AzureResourceManagerWrapper<PostgreSqlManager> {
	public AzureResourceManagerPostgreSql(String tenantId, String subscriptionId, PostgreSqlManager payload) {
		super( tenantId, subscriptionId, payload );
	}

	@Override
	public PostgreSqlManager getManager() {
		return super.getManager();
	}
}

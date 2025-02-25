/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.postgresqlflexibleserver.fluent.models.ServerInner;

/**
 * @author Martijn Sprengers
 * @since 1.0.3
 */
public class AzureResourcePostgreSqlFlexibleServer extends AzureResourceWrapper<ServerInner> {
	public AzureResourcePostgreSqlFlexibleServer(String tenantId, String resourceId, ServerInner payload) {
		super( tenantId, resourceId, payload );
	}

	@Override
	public ServerInner getPayload() {
		return super.getPayload();
	}
}

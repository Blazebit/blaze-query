/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.postgresqlflexibleserver.fluent.models.ServerInner;

import java.util.Map;

/**
 * @author Dimitar Prisadnikov
 * @since 1.0.4
 */
public class AzureResourcePostgreSqlFlexibleServerWithParameters extends AzureResourceWrapper<ServerInner> {
	private final Map<String, String> parameters;

	public AzureResourcePostgreSqlFlexibleServerWithParameters(
			String tenantId,
			String resourceId,
			ServerInner payload,
			Map<String, String> parameters
	) {
		super(tenantId, resourceId, payload);
		this.parameters = parameters;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	@Override
	public ServerInner getPayload() {
		return super.getPayload();
	}
}

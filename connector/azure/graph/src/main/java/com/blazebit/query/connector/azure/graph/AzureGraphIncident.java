/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.microsoft.graph.beta.models.security.Incident;

/**
 * * @author Martijn Sprengers
 * * @since 1.0.8
 */
public class AzureGraphIncident extends AzureGraphWrapper<Incident> {
	public AzureGraphIncident(String tenantId, Incident payload) {
		super(tenantId, payload);
	}

	@Override
	public Incident getPayload() {
		return super.getPayload();
	}
}

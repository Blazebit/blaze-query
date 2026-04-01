/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.sentinel;

import com.azure.resourcemanager.securityinsights.fluent.models.IncidentInner;

/**
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class SentinelIncident extends SentinelWrapper<IncidentInner> {

	public SentinelIncident(
			String tenantId,
			String subscriptionId,
			String resourceGroupName,
			String workspaceName,
			IncidentInner payload) {
		super( tenantId, subscriptionId, resourceGroupName, workspaceName, payload );
	}

	@Override
	public IncidentInner getPayload() {
		return super.getPayload();
	}
}

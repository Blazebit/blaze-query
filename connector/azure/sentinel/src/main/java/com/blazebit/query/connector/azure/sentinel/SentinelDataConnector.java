/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.sentinel;

import com.azure.resourcemanager.securityinsights.fluent.models.DataConnectorInner;

/**
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class SentinelDataConnector extends SentinelWrapper<DataConnectorInner> {

	public SentinelDataConnector(
			String tenantId,
			String subscriptionId,
			String resourceGroupName,
			String workspaceName,
			DataConnectorInner payload) {
		super( tenantId, subscriptionId, resourceGroupName, workspaceName, payload );
	}

	@Override
	public DataConnectorInner getPayload() {
		return super.getPayload();
	}
}

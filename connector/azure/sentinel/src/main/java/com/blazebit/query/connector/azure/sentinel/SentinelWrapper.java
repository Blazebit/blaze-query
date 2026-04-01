/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.sentinel;

/**
 * Base wrapper class for Microsoft Sentinel resource model objects, carrying workspace context.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public abstract class SentinelWrapper<T> {

	private final String tenantId;
	private final String subscriptionId;
	private final String resourceGroupName;
	private final String workspaceName;
	private final T payload;

	protected SentinelWrapper(
			String tenantId,
			String subscriptionId,
			String resourceGroupName,
			String workspaceName,
			T payload) {
		this.tenantId = tenantId;
		this.subscriptionId = subscriptionId;
		this.resourceGroupName = resourceGroupName;
		this.workspaceName = workspaceName;
		this.payload = payload;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public String getResourceGroupName() {
		return resourceGroupName;
	}

	public String getWorkspaceName() {
		return workspaceName;
	}

	public T getPayload() {
		return payload;
	}
}

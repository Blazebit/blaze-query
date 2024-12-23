/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public abstract class AzureResourceManagerWrapper<T> {

	private final String tenantId;
	private final String subscriptionId;
	private final String resourceGroupName;
	private final String resourceName;
	private final T payload;

	public AzureResourceManagerWrapper(String tenantId, String subscriptionId, String resourceGroupName, String resourceName, T payload) {
		this.tenantId = tenantId;
		this.subscriptionId = subscriptionId;
		this.resourceGroupName = resourceGroupName;
		this.resourceName = resourceName;
		this.payload = payload;
	}

	public AzureResourceManagerWrapper(String tenantId, String resourceId, T payload) {
		this.tenantId = tenantId;
		// Format: /subscriptions/{subscriptionId}/resourceGroups/{resourceGroupName}/providers/{resourceProviderNamespace}/{resourceType}/{resourceName}
		String[] splitParts = resourceId.split( "/" );
		assert splitParts.length >= 9;
		String subscriptionId = splitParts[2];
		String resourceGroupName = splitParts[4];
		String resourceName = splitParts[8];
		this.subscriptionId = subscriptionId;
		this.resourceGroupName = resourceGroupName;
		this.resourceName = resourceName;
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

	public String getResourceName() {
		return resourceName;
	}

	public T getPayload() {
		return payload;
	}
}

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
	private final T manager;

	public AzureResourceManagerWrapper(String tenantId, String subscriptionId, T manager) {
		this.tenantId = tenantId;
		this.subscriptionId = subscriptionId;
		this.manager = manager;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public T getManager() {
		return manager;
	}
}

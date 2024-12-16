/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public abstract class AzureGraphWrapper<T> {

	private final String tenantId;
	private final T payload;

	public AzureGraphWrapper(String tenantId, T payload) {
		this.tenantId = tenantId;
		this.payload = payload;
	}

	public String getTenantId() {
		return tenantId;
	}

	public T getPayload() {
		return payload;
	}
}

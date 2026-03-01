/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.base;

public abstract class GcpWrapper<T> {

	private final String resourceId;
	private final T payload;

	public GcpWrapper(String resourceId, T payload) {
		this.resourceId = resourceId;
		this.payload = payload;
	}

	public String getResourceId() {
		return resourceId;
	}

	public T getPayload() {
		return payload;
	}
}

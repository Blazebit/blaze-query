/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.directory;

public abstract class GoogleWrapper<T> {

	private final String resourceId;
	private final T payload;

	public GoogleWrapper(String resourceId, T payload) {
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

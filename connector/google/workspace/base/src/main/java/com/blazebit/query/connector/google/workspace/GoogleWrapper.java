/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.workspace;

/**
 * Base wrapper for Google Workspace API model objects.
 *
 * @param <T> the payload type
 * @author Blazebit
 * @since 1.0.0
 */
public abstract class GoogleWrapper<T> {

	private final String resourceId;
	private final T payload;

	/**
	 * Creates a new wrapper.
	 *
	 * @param resourceId the resource identifier
	 * @param payload the API model object
	 */
	public GoogleWrapper(String resourceId, T payload) {
		this.resourceId = resourceId;
		this.payload = payload;
	}

	/**
	 * Returns the resource identifier.
	 */
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * Returns the API model object.
	 */
	public T getPayload() {
		return payload;
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.workspace.endpointverification;

import com.google.api.services.directory.model.MobileDevice;

/**
 * Wrapper for a Google Workspace mobile device (Android/iOS).
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class GoogleMobileDevice {

	private final String customerId;
	private final MobileDevice mobileDevice;

	/**
	 * Creates a new wrapper.
	 *
	 * @param customerId the customer identifier
	 * @param mobileDevice the mobile device
	 */
	public GoogleMobileDevice(String customerId, MobileDevice mobileDevice) {
		this.customerId = customerId;
		this.mobileDevice = mobileDevice;
	}

	/**
	 * Returns the customer identifier.
	 */
	public String getCustomerId() {
		return customerId;
	}

	/**
	 * Returns the mobile device payload.
	 */
	public MobileDevice getMobileDevice() {
		return mobileDevice;
	}
}

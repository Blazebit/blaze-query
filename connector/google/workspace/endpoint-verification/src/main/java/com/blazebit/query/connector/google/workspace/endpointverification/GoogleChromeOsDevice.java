/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.workspace.endpointverification;

import com.google.api.services.directory.model.ChromeOsDevice;

/**
 * Wrapper for a Google Workspace ChromeOS device.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class GoogleChromeOsDevice {

	private final String customerId;
	private final ChromeOsDevice chromeOsDevice;

	/**
	 * Creates a new wrapper.
	 *
	 * @param customerId the customer identifier
	 * @param chromeOsDevice the ChromeOS device
	 */
	public GoogleChromeOsDevice(String customerId, ChromeOsDevice chromeOsDevice) {
		this.customerId = customerId;
		this.chromeOsDevice = chromeOsDevice;
	}

	/**
	 * Returns the customer identifier.
	 */
	public String getCustomerId() {
		return customerId;
	}

	/**
	 * Returns the ChromeOS device payload.
	 */
	public ChromeOsDevice getChromeOsDevice() {
		return chromeOsDevice;
	}
}

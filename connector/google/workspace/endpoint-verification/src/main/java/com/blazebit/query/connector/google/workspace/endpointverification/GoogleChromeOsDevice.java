/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.workspace.endpointverification;

import com.blazebit.query.connector.google.workspace.GoogleWrapper;
import com.google.api.services.directory.model.ChromeOsDevice;

/**
 * Wrapper for a Google Workspace ChromeOS device.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class GoogleChromeOsDevice extends GoogleWrapper<ChromeOsDevice> {

	/**
	 * Creates a new wrapper.
	 *
	 * @param resourceId the resource identifier
	 * @param chromeOsDevice the ChromeOS device
	 */
	public GoogleChromeOsDevice(String resourceId, ChromeOsDevice chromeOsDevice) {
		super( resourceId, chromeOsDevice );
	}

	@Override
	public ChromeOsDevice getPayload() {
		return super.getPayload();
	}
}

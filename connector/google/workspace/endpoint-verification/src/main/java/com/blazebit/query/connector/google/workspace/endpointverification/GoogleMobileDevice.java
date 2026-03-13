/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.workspace.endpointverification;

import com.blazebit.query.connector.google.workspace.GoogleWrapper;
import com.google.api.services.directory.model.MobileDevice;

/**
 * Wrapper for a Google Workspace mobile device (Android/iOS).
 *
 * @author Blazebit
 * @since 1.0.0
 */
public class GoogleMobileDevice extends GoogleWrapper<MobileDevice> {

	/**
	 * Creates a new wrapper.
	 *
	 * @param resourceId the resource identifier
	 * @param mobileDevice the mobile device
	 */
	public GoogleMobileDevice(String resourceId, MobileDevice mobileDevice) {
		super( resourceId, mobileDevice );
	}

	@Override
	public MobileDevice getPayload() {
		return super.getPayload();
	}
}

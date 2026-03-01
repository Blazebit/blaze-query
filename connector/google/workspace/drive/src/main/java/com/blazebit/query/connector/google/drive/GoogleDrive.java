/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.drive;

public class GoogleDrive extends GoogleWrapper<com.google.api.services.drive.model.Drive> {
	public GoogleDrive(String resourceId, com.google.api.services.drive.model.Drive drive) {
		super(resourceId, drive);
	}

	@Override
	public com.google.api.services.drive.model.Drive getPayload() {
		return super.getPayload();
	}
}

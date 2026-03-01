/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.directory;

import com.google.api.services.directory.model.RoleAssignment;

public class GoogleRoleAssignment extends GoogleWrapper<RoleAssignment> {
	public GoogleRoleAssignment(String resourceId, RoleAssignment roleAssignment) {
		super(resourceId, roleAssignment);
	}

	@Override
	public RoleAssignment getPayload() {
		return super.getPayload();
	}
}

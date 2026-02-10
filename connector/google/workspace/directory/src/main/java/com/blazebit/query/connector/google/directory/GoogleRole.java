/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.directory;

import com.google.api.services.directory.model.Role;

public class GoogleRole extends GoogleWrapper<Role> {
	public GoogleRole(String resourceId, Role role) {
		super(resourceId, role);
	}

	@Override
	public Role getPayload() {
		return super.getPayload();
	}
}

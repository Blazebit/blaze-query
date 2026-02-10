/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.iam;

import com.blazebit.query.connector.gcp.base.GcpWrapper;
import com.google.iam.admin.v1.Role;

public class GcpRole extends GcpWrapper<Role> {
	public GcpRole(String resourceId, Role role) {
		super(resourceId, role);
	}

	@Override
	public Role getPayload() {
		return super.getPayload();
	}
}

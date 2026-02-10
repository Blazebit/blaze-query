/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.base;

import com.google.cloud.resourcemanager.v3.Project;

public class GcpProject extends GcpWrapper<Project> {
	public GcpProject(String resourceId, Project project) {
		super(resourceId, project);
	}

	@Override
	public Project getPayload() {
		return super.getPayload();
	}
}

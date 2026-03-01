/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.base;

import com.google.cloud.resourcemanager.v3.Folder;

public class GcpFolder extends GcpWrapper<Folder> {
	public GcpFolder(String resourceId, Folder folder) {
		super(resourceId, folder);
	}

	@Override
	public Folder getPayload() {
		return super.getPayload();
	}
}

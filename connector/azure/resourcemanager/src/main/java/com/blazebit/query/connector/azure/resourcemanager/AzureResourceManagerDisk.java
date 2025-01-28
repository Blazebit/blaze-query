/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.compute.fluent.models.DiskInner;

/**
 * @author Martijn Sprengers
 * @since 1.0.2
 */
public class AzureResourceManagerDisk extends AzureResourceManagerWrapper<DiskInner> {
	public AzureResourceManagerDisk(String tenantId, String resourceId, DiskInner payload) {
		super(tenantId, resourceId, payload);
	}

	@Override
	public DiskInner getPayload() {
		return super.getPayload();
	}
}

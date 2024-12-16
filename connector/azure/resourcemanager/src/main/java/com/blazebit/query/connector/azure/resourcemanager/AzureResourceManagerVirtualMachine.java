/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.compute.fluent.models.VirtualMachineInner;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureResourceManagerVirtualMachine extends AzureResourceManagerWrapper<VirtualMachineInner> {
	public AzureResourceManagerVirtualMachine(String tenantId, String resourceId, VirtualMachineInner payload) {
		super( tenantId, resourceId, payload );
	}

	@Override
	public VirtualMachineInner getPayload() {
		return super.getPayload();
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.network.fluent.models.VirtualNetworkInner;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureResourceVirtualNetwork extends AzureResourceWrapper<VirtualNetworkInner> {
	public AzureResourceVirtualNetwork(String tenantId, String resourceId, VirtualNetworkInner payload) {
		super( tenantId, resourceId, payload );
	}

	@Override
	public VirtualNetworkInner getPayload() {
		return super.getPayload();
	}
}

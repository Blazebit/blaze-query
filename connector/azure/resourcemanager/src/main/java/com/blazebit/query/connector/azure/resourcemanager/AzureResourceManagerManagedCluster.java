/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.containerservice.fluent.models.ManagedClusterInner;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureResourceManagerManagedCluster extends AzureResourceManagerWrapper<ManagedClusterInner> {
	public AzureResourceManagerManagedCluster(String tenantId, String resourceId, ManagedClusterInner payload) {
		super( tenantId, resourceId, payload );
	}

	@Override
	public ManagedClusterInner getPayload() {
		return super.getPayload();
	}
}

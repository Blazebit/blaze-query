/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.resources.fluent.models.ResourceGroupInner;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureResourceManagerResourceGroup extends AzureResourceWrapper<ResourceGroupInner> {
	public AzureResourceManagerResourceGroup(String tenantId, String subscriptionId, String resourceGroupName, ResourceGroupInner payload) {
		super( tenantId, subscriptionId, resourceGroupName, resourceGroupName, payload );
	}

	@Override
	public ResourceGroupInner getPayload() {
		return super.getPayload();
	}
}

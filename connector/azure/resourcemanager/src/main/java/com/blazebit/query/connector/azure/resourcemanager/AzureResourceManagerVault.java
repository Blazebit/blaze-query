/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.keyvault.fluent.models.VaultInner;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureResourceManagerVault extends AzureResourceManagerWrapper<VaultInner> {
	public AzureResourceManagerVault(String tenantId, String resourceId, VaultInner payload) {
		super( tenantId, resourceId, payload );
	}

	@Override
	public VaultInner getPayload() {
		return super.getPayload();
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.storage.fluent.models.StorageAccountInner;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureResourceManagerStorageAccount extends AzureResourceManagerWrapper<StorageAccountInner> {
	public AzureResourceManagerStorageAccount(String tenantId, String resourceId, StorageAccountInner payload) {
		super( tenantId, resourceId, payload );
	}

	@Override
	public StorageAccountInner getPayload() {
		return super.getPayload();
	}
}

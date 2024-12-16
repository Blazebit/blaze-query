/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.storage.fluent.models.BlobServicePropertiesInner;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureResourceManagerBlobServiceProperties extends AzureResourceManagerWrapper<BlobServicePropertiesInner> {
	public AzureResourceManagerBlobServiceProperties(String tenantId, String resourceId, BlobServicePropertiesInner payload) {
		super( tenantId, resourceId, payload );
	}

	@Override
	public BlobServicePropertiesInner getPayload() {
		return super.getPayload();
	}
}

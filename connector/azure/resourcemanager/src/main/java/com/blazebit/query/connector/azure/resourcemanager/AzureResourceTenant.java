/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.resources.fluent.models.TenantIdDescriptionInner;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureResourceTenant extends AzureResourceWrapper<TenantIdDescriptionInner> {
	public AzureResourceTenant(String tenantId, TenantIdDescriptionInner payload) {
		super( tenantId, null, null, null, payload );
	}

	@Override
	public TenantIdDescriptionInner getPayload() {
		return super.getPayload();
	}
}

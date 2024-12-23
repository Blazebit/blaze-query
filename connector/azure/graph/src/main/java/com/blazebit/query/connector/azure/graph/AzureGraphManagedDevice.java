/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.microsoft.graph.beta.models.ManagedDevice;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureGraphManagedDevice extends AzureGraphWrapper<ManagedDevice> {
	public AzureGraphManagedDevice(String tenantId, ManagedDevice payload) {
		super( tenantId, payload );
	}

	@Override
	public ManagedDevice getPayload() {
		return super.getPayload();
	}
}

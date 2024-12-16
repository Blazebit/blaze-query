/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.microsoft.graph.beta.models.Application;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureGraphApplication extends AzureGraphWrapper<Application> {
	public AzureGraphApplication(String tenantId, Application payload) {
		super( tenantId, payload );
	}

	@Override
	public Application getPayload() {
		return super.getPayload();
	}
}

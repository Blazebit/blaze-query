/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.microsoft.graph.beta.models.security.Alert;

/**
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class AzureGraphAlert extends AzureGraphWrapper<Alert> {

	public AzureGraphAlert(String tenantId, Alert payload) {
		super( tenantId, payload );
	}

	@Override
	public Alert getPayload() {
		return super.getPayload();
	}
}

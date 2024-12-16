/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.microsoft.graph.beta.models.Organization;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureGraphOrganization extends AzureGraphWrapper<Organization> {
	public AzureGraphOrganization(String tenantId, Organization payload) {
		super( tenantId, payload );
	}

	@Override
	public Organization getPayload() {
		return super.getPayload();
	}
}

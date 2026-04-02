/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.microsoft.graph.beta.models.RiskyUser;

/**
 * Wrapper for a Microsoft Graph Beta {@link RiskyUser} enriched with the tenant ID.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class AzureGraphRiskyUser extends AzureGraphWrapper<RiskyUser> {
	public AzureGraphRiskyUser(String tenantId, RiskyUser payload) {
		super( tenantId, payload );
	}

	@Override
	public RiskyUser getPayload() {
		return super.getPayload();
	}
}

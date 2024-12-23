/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.microsoft.graph.beta.models.ConditionalAccessPolicy;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureGraphConditionalAccessPolicy extends AzureGraphWrapper<ConditionalAccessPolicy> {
	public AzureGraphConditionalAccessPolicy(String tenantId, ConditionalAccessPolicy payload) {
		super( tenantId, payload );
	}

	@Override
	public ConditionalAccessPolicy getPayload() {
		return super.getPayload();
	}
}

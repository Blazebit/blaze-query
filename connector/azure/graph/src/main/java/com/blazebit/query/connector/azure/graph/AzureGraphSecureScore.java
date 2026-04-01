/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.microsoft.graph.beta.models.SecureScore;

/**
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class AzureGraphSecureScore extends AzureGraphWrapper<SecureScore> {

	public AzureGraphSecureScore(String tenantId, SecureScore payload) {
		super( tenantId, payload );
	}

	@Override
	public SecureScore getPayload() {
		return super.getPayload();
	}
}

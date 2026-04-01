/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.microsoft.graph.beta.models.SecureScoreControlProfile;

/**
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class AzureGraphSecureScoreControlProfile extends AzureGraphWrapper<SecureScoreControlProfile> {

	public AzureGraphSecureScoreControlProfile(String tenantId, SecureScoreControlProfile payload) {
		super( tenantId, payload );
	}

	@Override
	public SecureScoreControlProfile getPayload() {
		return super.getPayload();
	}
}

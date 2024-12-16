/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.microsoft.graph.beta.models.User;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureGraphUser extends AzureGraphWrapper<User> {
	public AzureGraphUser(String tenantId, User payload) {
		super( tenantId, payload );
	}

	@Override
	public User getPayload() {
		return super.getPayload();
	}
}

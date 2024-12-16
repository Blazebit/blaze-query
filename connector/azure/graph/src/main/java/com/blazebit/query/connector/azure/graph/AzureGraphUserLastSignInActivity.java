/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureGraphUserLastSignInActivity extends AzureGraphWrapper<UserLastSignInActivity> {
	public AzureGraphUserLastSignInActivity(String tenantId, UserLastSignInActivity payload) {
		super( tenantId, payload );
	}

	@Override
	public UserLastSignInActivity getPayload() {
		return super.getPayload();
	}
}

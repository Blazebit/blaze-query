/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.resources.fluent.models.SubscriptionInner;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureResourceManagerSubscription extends AzureResourceManagerWrapper<SubscriptionInner> {
	public AzureResourceManagerSubscription(String tenantId, String subscriptionId, SubscriptionInner payload) {
		super( tenantId, subscriptionId, null, null, payload );
	}

	@Override
	public SubscriptionInner getPayload() {
		return super.getPayload();
	}
}

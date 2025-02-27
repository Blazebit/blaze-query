/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.network.fluent.models.NetworkSecurityGroupInner;

/**
 * @author Martijn Sprengers
 * @since 1.0.2
 */
public class AzureResourceNetworkSecurityGroup extends AzureResourceWrapper<NetworkSecurityGroupInner> {
	public AzureResourceNetworkSecurityGroup(String tenantId, String resourceId, NetworkSecurityGroupInner payload) {
		super( tenantId, resourceId, payload );
	}

	@Override
	public NetworkSecurityGroupInner getPayload() {
		return super.getPayload();
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.authorization.fluent.models.RoleAssignmentInner;

/**
 * Wraps an Azure {@link RoleAssignmentInner} enriched with tenant and subscription context.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class AzureResourceRoleAssignment extends AzureResourceWrapper<RoleAssignmentInner> {

	public AzureResourceRoleAssignment(
			String tenantId,
			String subscriptionId,
			String resourceGroupName,
			String roleAssignmentName,
			RoleAssignmentInner payload) {
		super( tenantId, subscriptionId, resourceGroupName, roleAssignmentName, payload );
	}

	@Override
	public RoleAssignmentInner getPayload() {
		return super.getPayload();
	}
}

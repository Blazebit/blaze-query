/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

/**
 * Represents an Azure role assignment from the ARM Authorization API.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public record AzureResourceRoleAssignment(
		String tenantId,
		String roleAssignmentId,
		String subscriptionId,
		String assignmentScope,
		String principalId,
		String principalType,
		String roleDefinitionId,
		String description,
		String createdBy) {
}

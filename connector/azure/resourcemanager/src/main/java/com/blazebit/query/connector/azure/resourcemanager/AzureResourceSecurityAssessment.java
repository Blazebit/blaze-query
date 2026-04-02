/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

/**
 * Represents a security assessment from the Azure Resource Graph
 * {@code securityresources} table (type {@code microsoft.security/assessments}).
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public record AzureResourceSecurityAssessment(
		String tenantId,
		String assessmentId,
		String subscriptionId,
		String resourceId,
		String displayName,
		String statusCode,
		String severity) {
}

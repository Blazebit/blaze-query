/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a VM patch assessment result from the Azure Resource Graph
 * {@code patchassessmentresources} table.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public record AzureResourcePatchAssessmentResult(
		String tenantId,
		String resourceId,
		String subscriptionId,
		String resourceGroupName,
		String vmName,
		String lastModifiedDateTime,
		String startDateTime,
		String statusCode,
		Boolean rebootPending,
		Integer criticalAndSecurityPatchCount,
		Integer otherPatchCount,
		Integer criticalPatchCount,
		Integer securityPatchCount) {

	static AzureResourcePatchAssessmentResult fromJson(String tenantId, String resourceId, JsonNode properties) {
		String subscriptionId = null;
		String resourceGroupName = null;
		String vmName = null;
		if ( resourceId != null ) {
			// /subscriptions/{sub}/resourceGroups/{rg}/providers/.../{name}
			String[] parts = resourceId.split( "/" );
			if ( parts.length >= 9 ) {
				subscriptionId = parts[2];
				resourceGroupName = parts[4];
				vmName = parts[8];
			}
		}

		String lastModifiedDateTime = textOrNull( properties, "lastModifiedDateTime" );
		String startDateTime = textOrNull( properties, "startDateTime" );
		Boolean rebootPending = boolOrNull( properties, "rebootPending" );
		Integer criticalAndSecurityCount = intOrNull( properties, "criticalAndSecurityPatchCount" );
		Integer otherCount = intOrNull( properties, "otherPatchCount" );

		String statusCode = null;
		if ( properties != null ) {
			JsonNode statusNode = properties.get( "status" );
			if ( statusNode != null && !statusNode.isNull() ) {
				statusCode = statusNode.isObject() ? textOrNull( statusNode, "code" ) : statusNode.asText();
			}
		}

		Integer criticalCount = null;
		Integer securityCount = null;
		if ( properties != null ) {
			JsonNode byClass = properties.get( "availablePatchCountByClassification" );
			if ( byClass != null && !byClass.isNull() ) {
				criticalCount = intOrNull( byClass, "critical" );
				securityCount = intOrNull( byClass, "security" );
			}
		}

		return new AzureResourcePatchAssessmentResult(
				tenantId, resourceId, subscriptionId, resourceGroupName, vmName,
				lastModifiedDateTime, startDateTime, statusCode, rebootPending,
				criticalAndSecurityCount, otherCount, criticalCount, securityCount );
	}

	private static String textOrNull(JsonNode node, String field) {
		if ( node == null ) {
			return null;
		}
		JsonNode f = node.get( field );
		return f == null || f.isNull() ? null : f.asText();
	}

	private static Boolean boolOrNull(JsonNode node, String field) {
		if ( node == null ) {
			return null;
		}
		JsonNode f = node.get( field );
		return f == null || f.isNull() ? null : f.asBoolean();
	}

	private static Integer intOrNull(JsonNode node, String field) {
		if ( node == null ) {
			return null;
		}
		JsonNode f = node.get( field );
		return f == null || f.isNull() ? null : f.asInt();
	}
}

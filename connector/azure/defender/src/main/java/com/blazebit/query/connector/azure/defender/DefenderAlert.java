/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.defender;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents an alert from the Microsoft Defender for Endpoint API.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public record DefenderAlert(
		String tenantId,
		String id,
		String incidentId,
		String investigationId,
		String assignedTo,
		String severity,
		String status,
		String classification,
		String determination,
		String investigationState,
		String detectionSource,
		String detectorId,
		String category,
		String title,
		String description,
		String alertCreationTime,
		String firstEventTime,
		String lastEventTime,
		String lastUpdateTime,
		String resolvedTime,
		String machineId,
		String computerDnsName,
		String rbacGroupName,
		String aadTenantId,
		String threatName,
		String mitreTechniques,
		String threatFamilyName) {

	static DefenderAlert fromJson(String tenantId, JsonNode node) {
		return new DefenderAlert(
				tenantId,
				textOrNull( node, "id" ),
				textOrNull( node, "incidentId" ),
				textOrNull( node, "investigationId" ),
				textOrNull( node, "assignedTo" ),
				textOrNull( node, "severity" ),
				textOrNull( node, "status" ),
				textOrNull( node, "classification" ),
				textOrNull( node, "determination" ),
				textOrNull( node, "investigationState" ),
				textOrNull( node, "detectionSource" ),
				textOrNull( node, "detectorId" ),
				textOrNull( node, "category" ),
				textOrNull( node, "title" ),
				textOrNull( node, "description" ),
				textOrNull( node, "alertCreationTime" ),
				textOrNull( node, "firstEventTime" ),
				textOrNull( node, "lastEventTime" ),
				textOrNull( node, "lastUpdateTime" ),
				textOrNull( node, "resolvedTime" ),
				textOrNull( node, "machineId" ),
				textOrNull( node, "computerDnsName" ),
				textOrNull( node, "rbacGroupName" ),
				textOrNull( node, "aadTenantId" ),
				textOrNull( node, "threatName" ),
				arrayToCommaSeparated( node, "mitreTechniques" ),
				textOrNull( node, "threatFamilyName" ) );
	}

	private static String textOrNull(JsonNode node, String field) {
		JsonNode f = node.get( field );
		return f == null || f.isNull() ? null : f.asText();
	}

	private static String arrayToCommaSeparated(JsonNode node, String field) {
		JsonNode f = node.get( field );
		if ( f == null || f.isNull() || !f.isArray() || f.isEmpty() ) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for ( JsonNode element : f ) {
			if ( sb.length() > 0 ) {
				sb.append( ',' );
			}
			sb.append( element.asText() );
		}
		return sb.toString();
	}
}

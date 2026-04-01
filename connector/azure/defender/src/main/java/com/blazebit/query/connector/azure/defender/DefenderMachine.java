/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.defender;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a machine (device) from the Microsoft Defender for Endpoint API.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public record DefenderMachine(
		String tenantId,
		String id,
		String computerDnsName,
		String osPlatform,
		String osVersion,
		String osProcessor,
		String lastSeen,
		String firstSeen,
		String agentVersion,
		String status,
		String rbacGroupName,
		String onboardingStatus,
		String riskScore,
		String exposureLevel,
		Boolean isAadJoined,
		String aadDeviceId,
		String defenderAvStatus,
		String managedBy) {

	static DefenderMachine fromJson(String tenantId, JsonNode node) {
		return new DefenderMachine(
				tenantId,
				textOrNull( node, "id" ),
				textOrNull( node, "computerDnsName" ),
				textOrNull( node, "osPlatform" ),
				textOrNull( node, "osVersion" ),
				textOrNull( node, "osProcessor" ),
				textOrNull( node, "lastSeen" ),
				textOrNull( node, "firstSeen" ),
				textOrNull( node, "agentVersion" ),
				textOrNull( node, "status" ),
				textOrNull( node, "rbacGroupName" ),
				textOrNull( node, "onboardingStatus" ),
				textOrNull( node, "riskScore" ),
				textOrNull( node, "exposureLevel" ),
				boolOrNull( node, "isAadJoined" ),
				textOrNull( node, "aadDeviceId" ),
				textOrNull( node, "defenderAvStatus" ),
				textOrNull( node, "managedBy" ) );
	}

	private static String textOrNull(JsonNode node, String field) {
		JsonNode f = node.get( field );
		return f == null || f.isNull() ? null : f.asText();
	}

	private static Boolean boolOrNull(JsonNode node, String field) {
		JsonNode f = node.get( field );
		return f == null || f.isNull() ? null : f.asBoolean();
	}
}

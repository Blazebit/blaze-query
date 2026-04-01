/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.defender;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a security recommendation from the Microsoft Defender for Endpoint API.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public record DefenderRecommendation(
		String id,
		String productName,
		String recommendationName,
		Long weaknesses,
		String vendor,
		String recommendedVersion,
		String recommendationCategory,
		String subCategory,
		Double severityScore,
		Boolean publicExploit,
		Boolean activeAlert,
		String associatedThreats,
		String remediationType,
		String status,
		Double configScoreImpact,
		Double exposureImpact,
		Long totalMachineCount,
		Long exposedMachinesCount,
		Boolean nonProductivityImpactedAssets,
		String relatedComponent) {

	static DefenderRecommendation fromJson(JsonNode node) {
		return new DefenderRecommendation(
				textOrNull( node, "id" ),
				textOrNull( node, "productName" ),
				textOrNull( node, "recommendationName" ),
				longOrNull( node, "weaknesses" ),
				textOrNull( node, "vendor" ),
				textOrNull( node, "recommendedVersion" ),
				textOrNull( node, "recommendationCategory" ),
				textOrNull( node, "subCategory" ),
				doubleOrNull( node, "severityScore" ),
				boolOrNull( node, "publicExploit" ),
				boolOrNull( node, "activeAlert" ),
				arrayToCommaSeparated( node, "associatedThreats" ),
				textOrNull( node, "remediationType" ),
				textOrNull( node, "status" ),
				doubleOrNull( node, "configScoreImpact" ),
				doubleOrNull( node, "exposureImpact" ),
				longOrNull( node, "totalMachineCount" ),
				longOrNull( node, "exposedMachinesCount" ),
				boolOrNull( node, "nonProductivityImpactedAssets" ),
				textOrNull( node, "relatedComponent" ) );
	}

	private static String textOrNull(JsonNode node, String field) {
		JsonNode f = node.get( field );
		return f == null || f.isNull() ? null : f.asText();
	}

	private static Boolean boolOrNull(JsonNode node, String field) {
		JsonNode f = node.get( field );
		return f == null || f.isNull() ? null : f.asBoolean();
	}

	private static Double doubleOrNull(JsonNode node, String field) {
		JsonNode f = node.get( field );
		return f == null || f.isNull() ? null : f.asDouble();
	}

	private static Long longOrNull(JsonNode node, String field) {
		JsonNode f = node.get( field );
		return f == null || f.isNull() ? null : f.asLong();
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

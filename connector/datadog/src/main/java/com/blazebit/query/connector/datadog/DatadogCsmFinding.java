/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v2.model.Finding;
import com.datadog.api.client.v2.model.FindingAttributes;

import java.util.List;

/**
 * Represents a Datadog Cloud Security Management (CSM) finding.
 * Each finding is a specific misconfiguration or vulnerability detected on a cloud resource.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public record DatadogCsmFinding(
		String id,
		String resource,
		String resourceType,
		String ruleId,
		String ruleName,
		String status,
		String evaluation,
		Boolean muted,
		String muteReason,
		String vulnerabilityType,
		List<String> tags,
		Long evaluationChangedAt,
		Long resourceDiscoveryDate
) {

	/**
	 * Maps a Datadog SDK {@link Finding} to a {@link DatadogCsmFinding} record.
	 */
	public static DatadogCsmFinding from(Finding finding) {
		FindingAttributes attr = finding.getAttributes();
		if ( attr == null ) {
			return new DatadogCsmFinding( finding.getId(), null, null, null, null,
					null, null, null, null, null, List.of(), null, null );
		}
		String ruleId = attr.getRule() != null ? attr.getRule().getId() : null;
		String ruleName = attr.getRule() != null ? attr.getRule().getName() : null;
		String status = attr.getStatus() != null ? attr.getStatus().getValue() : null;
		String evaluation = attr.getEvaluation() != null ? attr.getEvaluation().getValue() : null;
		Boolean muted = attr.getMute() != null ? attr.getMute().getMuted() : null;
		String muteReason = attr.getMute() != null && attr.getMute().getReason() != null
				? attr.getMute().getReason().getValue() : null;
		String vulnType = attr.getVulnerabilityType() != null ? attr.getVulnerabilityType().getValue() : null;
		return new DatadogCsmFinding(
				finding.getId(),
				attr.getResource(),
				attr.getResourceType(),
				ruleId,
				ruleName,
				status,
				evaluation,
				muted,
				muteReason,
				vulnType,
				attr.getTags() != null ? attr.getTags() : List.of(),
				attr.getEvaluationChangedAt(),
				attr.getResourceDiscoveryDate()
		);
	}
}

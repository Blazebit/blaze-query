/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v2.model.SecurityMonitoringRuleResponse;
import com.datadog.api.client.v2.model.SecurityMonitoringSignalRuleResponse;
import com.datadog.api.client.v2.model.SecurityMonitoringStandardRuleResponse;

import java.util.List;

/**
 * Represents a Datadog Security Monitoring detection rule. Used for compliance checks
 * such as verifying that critical detection rules are enabled.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public record DatadogSecurityMonitoringRule(
		String id,
		String name,
		String message,
		Boolean enabled,
		List<String> tags,
		String type
) {

	/**
	 * Maps a Datadog SDK {@link SecurityMonitoringRuleResponse} to a {@link DatadogSecurityMonitoringRule} record.
	 */
	public static DatadogSecurityMonitoringRule from(SecurityMonitoringRuleResponse response) {
		Object actual = response.getActualInstance();
		if ( actual instanceof SecurityMonitoringStandardRuleResponse rule ) {
			return new DatadogSecurityMonitoringRule(
					rule.getId(),
					rule.getName(),
					rule.getMessage(),
					rule.getIsEnabled(),
					rule.getTags() != null ? rule.getTags() : List.of(),
					rule.getType() != null ? rule.getType().getValue() : null
			);
		}
		if ( actual instanceof SecurityMonitoringSignalRuleResponse rule ) {
			return new DatadogSecurityMonitoringRule(
					rule.getId(),
					rule.getName(),
					rule.getMessage(),
					rule.getIsEnabled(),
					rule.getTags() != null ? rule.getTags() : List.of(),
					rule.getType() != null ? rule.getType().getValue() : null
			);
		}
		return null;
	}
}

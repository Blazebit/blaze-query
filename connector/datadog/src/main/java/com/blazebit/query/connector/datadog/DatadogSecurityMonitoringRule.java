/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v2.model.SecurityMonitoringRuleResponse;
import com.datadog.api.client.v2.model.SecurityMonitoringSignalRuleResponse;
import com.datadog.api.client.v2.model.SecurityMonitoringStandardRuleResponse;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a Datadog Security Monitoring detection rule. Used for compliance checks
 * such as verifying that critical detection rules are enabled.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
 */
public record DatadogSecurityMonitoringRule(
		String id,
		String name,
		String message,
		Boolean enabled,
		List<String> tags,
		String type
) {

	private static final Logger LOG = Logger.getLogger( DatadogSecurityMonitoringRule.class.getName() );

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
		LOG.log( Level.WARNING, "Unrecognized SecurityMonitoringRuleResponse type: {0}, skipping rule",
				response.getActualInstance() != null ? response.getActualInstance().getClass().getName() : "null" );
		return null;
	}
}

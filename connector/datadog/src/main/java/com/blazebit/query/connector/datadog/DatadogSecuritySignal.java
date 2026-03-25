/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v2.model.SecurityMonitoringSignal;
import com.datadog.api.client.v2.model.SecurityMonitoringSignalAttributes;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Represents a Datadog Security Monitoring signal (threat detection event).
 * Signals are generated when detection rules match activity in logs or other data sources.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
 */
public record DatadogSecuritySignal(
		String id,
		String message,
		OffsetDateTime timestamp,
		List<String> tags
) {

	/**
	 * Maps a Datadog SDK {@link SecurityMonitoringSignal} to a {@link DatadogSecuritySignal} record.
	 */
	public static DatadogSecuritySignal from(SecurityMonitoringSignal signal) {
		SecurityMonitoringSignalAttributes attr = signal.getAttributes();
		if ( attr == null ) {
			return new DatadogSecuritySignal( signal.getId(), null, null, List.of() );
		}
		return new DatadogSecuritySignal(
				signal.getId(),
				attr.getMessage(),
				attr.getTimestamp(),
				attr.getTags() != null ? attr.getTags() : List.of()
		);
	}
}

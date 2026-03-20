/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v1.model.Monitor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Represents a Datadog Monitor. Monitors are the primary mechanism for tracking
 * infrastructure health and alerting on anomalies.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public record DatadogMonitor(
		Long id,
		String name,
		String type,
		String overallState,
		Long priority,
		String query,
		String message,
		List<String> tags,
		OffsetDateTime created,
		OffsetDateTime modified
) {

	/**
	 * Maps a Datadog SDK {@link Monitor} to a {@link DatadogMonitor} record.
	 */
	public static DatadogMonitor from(Monitor monitor) {
		return new DatadogMonitor(
				monitor.getId(),
				monitor.getName(),
				monitor.getType() != null ? monitor.getType().toString() : null,
				monitor.getOverallState() != null ? monitor.getOverallState().toString() : null,
				monitor.getPriority(),
				monitor.getQuery(),
				monitor.getMessage(),
				monitor.getTags() != null ? monitor.getTags() : List.of(),
				monitor.getCreated(),
				monitor.getModified()
		);
	}
}

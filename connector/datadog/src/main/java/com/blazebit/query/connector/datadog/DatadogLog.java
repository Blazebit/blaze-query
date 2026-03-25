/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v2.model.Log;
import com.datadog.api.client.v2.model.LogAttributes;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Represents a single Datadog log entry.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
 */
public record DatadogLog(
		String id,
		String host,
		String service,
		String status,
		String message,
		OffsetDateTime timestamp,
		List<String> tags
) {

	/**
	 * Maps a Datadog SDK {@link Log} to a {@link DatadogLog} record.
	 */
	public static DatadogLog from(Log log) {
		LogAttributes attrs = log.getAttributes();
		return new DatadogLog(
				log.getId(),
				attrs != null ? attrs.getHost() : null,
				attrs != null ? attrs.getService() : null,
				attrs != null ? attrs.getStatus() : null,
				attrs != null ? attrs.getMessage() : null,
				attrs != null ? attrs.getTimestamp() : null,
				attrs != null && attrs.getTags() != null ? attrs.getTags() : List.of()
		);
	}
}

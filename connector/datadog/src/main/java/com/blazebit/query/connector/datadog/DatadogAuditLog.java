/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v2.model.AuditLogsEvent;
import com.datadog.api.client.v2.model.AuditLogsEventAttributes;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Represents a Datadog Audit Trail event. Records who performed what action in Datadog,
 * useful for compliance evidence and detecting unauthorized configuration changes.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public record DatadogAuditLog(
		String id,
		String message,
		String service,
		OffsetDateTime timestamp,
		List<String> tags
) {

	/**
	 * Maps a Datadog SDK {@link AuditLogsEvent} to a {@link DatadogAuditLog} record.
	 */
	public static DatadogAuditLog from(AuditLogsEvent event) {
		AuditLogsEventAttributes attr = event.getAttributes();
		if ( attr == null ) {
			return new DatadogAuditLog( event.getId(), null, null, null, List.of() );
		}
		return new DatadogAuditLog(
				event.getId(),
				attr.getMessage(),
				attr.getService(),
				attr.getTimestamp(),
				attr.getTags() != null ? attr.getTags() : List.of()
		);
	}
}

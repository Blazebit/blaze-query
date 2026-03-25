/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v2.model.DowntimeResponseAttributes;
import com.datadog.api.client.v2.model.DowntimeResponseData;

import java.time.OffsetDateTime;

/**
 * Represents a Datadog monitor downtime (scheduled silence). Active downtimes suppress
 * monitor alerts and represent potential blind spots in alerting coverage.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
 */
public record DatadogMonitorDowntime(
		String id,
		String scope,
		String status,
		String message,
		OffsetDateTime created,
		OffsetDateTime modified,
		OffsetDateTime canceled
) {

	/**
	 * Maps a Datadog SDK {@link DowntimeResponseData} to a {@link DatadogMonitorDowntime} record.
	 */
	public static DatadogMonitorDowntime from(DowntimeResponseData data) {
		DowntimeResponseAttributes attr = data.getAttributes();
		if ( attr == null ) {
			return new DatadogMonitorDowntime( data.getId(), null, null, null, null, null, null );
		}
		return new DatadogMonitorDowntime(
				data.getId(),
				attr.getScope(),
				attr.getStatus() != null ? attr.getStatus().getValue() : null,
				attr.getMessage(),
				attr.getCreated(),
				attr.getModified(),
				attr.getCanceled()
		);
	}
}

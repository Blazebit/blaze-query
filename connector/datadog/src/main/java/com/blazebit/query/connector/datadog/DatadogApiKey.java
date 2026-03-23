/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v2.model.PartialAPIKey;
import com.datadog.api.client.v2.model.PartialAPIKeyAttributes;

import java.time.OffsetDateTime;

/**
 * Represents a Datadog API key. Used for compliance checks such as identifying
 * stale keys, keys without a recent last-used date, and keys with remote config access.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public record DatadogApiKey(
		String id,
		String name,
		String last4,
		String category,
		String createdAt,
		OffsetDateTime dateLastUsed,
		String modifiedAt,
		Boolean remoteConfigReadEnabled
) {

	/**
	 * Maps a Datadog SDK {@link PartialAPIKey} to a {@link DatadogApiKey} record.
	 */
	public static DatadogApiKey from(PartialAPIKey key) {
		PartialAPIKeyAttributes attr = key.getAttributes();
		if ( attr == null ) {
			return new DatadogApiKey( key.getId(), null, null, null, null, null, null, null );
		}
		return new DatadogApiKey(
				key.getId(),
				attr.getName(),
				attr.getLast4(),
				attr.getCategory(),
				attr.getCreatedAt(),
				attr.getDateLastUsed(),
				attr.getModifiedAt(),
				attr.getRemoteConfigReadEnabled()
		);
	}
}

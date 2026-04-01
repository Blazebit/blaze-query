/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v2.model.PartialApplicationKey;
import com.datadog.api.client.v2.model.PartialApplicationKeyAttributes;

import java.util.List;

/**
 * Represents a Datadog Application Key. Application keys complement API keys and are
 * scoped to the creating user. Used for compliance checks on key age, last use, and scope.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
 */
public record DatadogApplicationKey(
		String id,
		String name,
		String last4,
		String createdAt,
		String lastUsedAt,
		List<String> scopes
) {

	/**
	 * Maps a Datadog SDK {@link PartialApplicationKey} to a {@link DatadogApplicationKey} record.
	 */
	public static DatadogApplicationKey from(PartialApplicationKey key) {
		PartialApplicationKeyAttributes attr = key.getAttributes();
		if ( attr == null ) {
			return new DatadogApplicationKey( key.getId(), null, null, null, null, List.of() );
		}
		return new DatadogApplicationKey(
				key.getId(),
				attr.getName(),
				attr.getLast4(),
				attr.getCreatedAt(),
				attr.getLastUsedAt(),
				attr.getScopes() != null ? attr.getScopes() : List.of()
		);
	}
}

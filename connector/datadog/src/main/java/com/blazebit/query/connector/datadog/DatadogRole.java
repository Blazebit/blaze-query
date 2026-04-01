/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v2.model.Role;
import com.datadog.api.client.v2.model.RoleAttributes;

import java.time.OffsetDateTime;

/**
 * Represents a Datadog RBAC role. Used for compliance checks such as
 * identifying roles with elevated permissions and their user counts.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
 */
public record DatadogRole(
		String id,
		String name,
		Long userCount,
		OffsetDateTime createdAt,
		OffsetDateTime modifiedAt
) {

	/**
	 * Maps a Datadog SDK {@link Role} to a {@link DatadogRole} record.
	 */
	public static DatadogRole from(Role role) {
		RoleAttributes attr = role.getAttributes();
		if ( attr == null ) {
			return new DatadogRole( role.getId(), null, null, null, null );
		}
		return new DatadogRole(
				role.getId(),
				attr.getName(),
				attr.getUserCount(),
				attr.getCreatedAt(),
				attr.getModifiedAt()
		);
	}
}

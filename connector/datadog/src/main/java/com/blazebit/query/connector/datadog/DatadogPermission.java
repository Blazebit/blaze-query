/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v2.model.Permission;
import com.datadog.api.client.v2.model.PermissionAttributes;

/**
 * Represents a Datadog RBAC permission. The full permission catalog can be joined with
 * role assignments to answer which roles hold which permissions.
 *
 * @author Martijn Sprengers
 * @since 2.4.2
 */
public record DatadogPermission(
		String id,
		String name,
		String displayName,
		String description,
		String groupName,
		String displayType,
		Boolean restricted
) {

	/**
	 * Maps a Datadog SDK {@link Permission} to a {@link DatadogPermission} record.
	 */
	public static DatadogPermission from(Permission permission) {
		PermissionAttributes attr = permission.getAttributes();
		if ( attr == null ) {
			return new DatadogPermission( permission.getId(), null, null, null, null, null, null );
		}
		return new DatadogPermission(
				permission.getId(),
				attr.getName(),
				attr.getDisplayName(),
				attr.getDescription(),
				attr.getGroupName(),
				attr.getDisplayType(),
				attr.getRestricted()
		);
	}
}

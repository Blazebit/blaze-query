/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import java.util.UUID;

public class ServicePlan {
	private final UUID id;
	private final String stringId;
	private final String displayName;
	private final UUID parentId;
	private final String parentStringId;
	private final String parentDisplayName;

	public ServicePlan(UUID id, String stringId, String displayName, UUID parentId, String parentStringId, String parentDisplayName) {
		this.id = id;
		this.stringId = stringId;
		this.displayName = displayName;
		this.parentId = parentId;
		this.parentStringId = parentStringId;
		this.parentDisplayName = parentDisplayName;
	}

	public UUID getId() {
		return id;
	}

	public String getStringId() {
		return stringId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public UUID getParentId() {
		return parentId;
	}

	public String getParentDisplayName() {
		return parentDisplayName;
	}

	public String getParentStringId() {
		return parentStringId;
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.directory;

import com.google.api.services.directory.model.Group;

public class GoogleGroup extends GoogleWrapper<Group> {
	public GoogleGroup(String resourceId, Group group) {
		super(resourceId, group);
	}

	@Override
	public Group getPayload() {
		return super.getPayload();
	}
}

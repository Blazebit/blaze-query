/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.directory;

import com.google.api.services.directory.model.User;

public class GoogleUser extends GoogleWrapper<User> {
	public GoogleUser(String resourceId, User user) {
		super(resourceId, user);
	}

	@Override
	public User getPayload() {
		return super.getPayload();
	}
}

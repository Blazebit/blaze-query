/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.datadog.api.client.v2.model.User;
import com.datadog.api.client.v2.model.UserAttributes;

/**
 * Represents a Datadog user account. Used for compliance checks such as
 * MFA enforcement, disabled accounts, and service account identification.
 *
 * @author Blazebit
 * @since 1.0.0
 */
public record DatadogUser(
		String id,
		String name,
		String email,
		String handle,
		String status,
		Boolean mfaEnabled,
		Boolean disabled,
		Boolean serviceAccount
) {

	/**
	 * Maps a Datadog SDK {@link User} to a {@link DatadogUser} record.
	 */
	public static DatadogUser from(User user) {
		UserAttributes attr = user.getAttributes();
		if ( attr == null ) {
			return new DatadogUser( user.getId(), null, null, null, null, null, null, null );
		}
		return new DatadogUser(
				user.getId(),
				attr.getName(),
				attr.getEmail(),
				attr.getHandle(),
				attr.getStatus(),
				attr.getMfaEnabled(),
				attr.getDisabled(),
				attr.getServiceAccount()
		);
	}
}

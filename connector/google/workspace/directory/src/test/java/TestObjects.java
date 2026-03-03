/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
import com.blazebit.query.connector.google.directory.GoogleUser;
import com.google.api.client.util.DateTime;
import com.google.api.services.directory.model.User;

import java.time.Instant;

public final class TestObjects {
	private TestObjects() {
	}

	public static GoogleUser staleUser() {
		User user = new User();
		user.setId("stale-user-id");
		user.setSuspended(false);
		user.setCreationTime( new DateTime("2020-01-01T00:00:00.000Z") );
		user.setLastLoginTime(  new DateTime("2023-01-01T00:00:00.000Z") );

		// MFA fields (enrolled + enforced)
		user.setIsEnrolledIn2Sv(true);
		user.setIsEnforcedIn2Sv(true);

		return new GoogleUser( user.getId(), user );
	}

	public static GoogleUser activeUser() {
		User user = new User();
		user.setId("active-user-id");
		user.setSuspended(false);
		user.setCreationTime( new DateTime("2022-01-01T00:00:00.000Z") );
		// set lastLoginTime to now
		user.setLastLoginTime(  new DateTime(Instant.now().toEpochMilli()) );
		user.setCreationTime( new DateTime("2025-01-01T00:00:00.000Z") );

		// MFA fields (enrolled + enforced)
		user.setIsEnrolledIn2Sv(true);
		user.setIsEnforcedIn2Sv(true);

		return new GoogleUser( user.getId(), user );
	}

	public static GoogleUser suspendedUser() {
		User user = new User();
		user.setId("suspended-user-id");
		user.setSuspended(true);
		user.setCreationTime( new DateTime("2020-01-01T00:00:00.000Z") );
		user.setLastLoginTime(  new DateTime("2023-01-01T00:00:00.000Z") );

		// MFA fields don't matter, but set for completeness
		user.setIsEnrolledIn2Sv(false);
		user.setIsEnforcedIn2Sv(false);

		return new GoogleUser( user.getId(), user );
	}

	public static GoogleUser activeUserWithoutMfa() {
		User user = new User();
		user.setId("active-user-no-mfa-id");
		user.setSuspended(false);
		user.setCreationTime(new DateTime("2025-01-01T00:00:00.000Z"));
		user.setLastLoginTime(new DateTime(Instant.now().toEpochMilli()));

		// MFA fields (not enrolled + not enforced)
		user.setIsEnrolledIn2Sv(false);
		user.setIsEnforcedIn2Sv(false);

		return new GoogleUser( user.getId(), user );
	}
}

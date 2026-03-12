/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.directory;

import com.blazebit.query.QueryContext;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GoogleWorkSpaceTests {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new GoogleDirectorySchemaProvider() );
		builder.registerSchemaObjectAlias( GoogleUser.class, "GoogleUser" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_google_users() {
		try (var session = CONTEXT.createSession()) {
			session.put(
				GoogleUser.class,
					List.of(
							TestObjects.staleUser(),
							TestObjects.activeUser(),
							TestObjects.suspendedUser()
					)
			);

			var typedQuery =
				session.createQuery( "select u.* from GoogleUser u", new com.blazebit.query.TypeReference<java.util.Map<String, Object>>() {
				} );

			assertThat( typedQuery.getResultList() ).hasSize( 3 );
		}
	}

	@Test
	void should_return_suspended_users() {
		try (var session = CONTEXT.createSession()) {
			session.put(
				GoogleUser.class,
					List.of(
							TestObjects.staleUser(),
							TestObjects.activeUser(),
							TestObjects.suspendedUser()
					)
			);

			var typedQuery =
				session.createQuery( "select u.* from GoogleUser u where u.payload.suspended = true", new com.blazebit.query.TypeReference<java.util.Map<String, Object>>() {
				} );

			assertThat( typedQuery.getResultList() ).hasSize( 1 );
		}
	}

	@Test
	void should_return_active_users() {
		try (var session = CONTEXT.createSession()) {
			session.put(
				GoogleUser.class,
					List.of(
							TestObjects.staleUser(),
							TestObjects.activeUser(),
							TestObjects.suspendedUser()
					)
			);

			long oneYearAgoMillis =
					OffsetDateTime.now( ZoneOffset.UTC)
							.minusYears(1)
							.toInstant()
							.toEpochMilli();

			var typedQuery =
				session.createQuery( "select u.* from GoogleUser u where u.payload.suspended = false and u.payload.lastLoginTime is not null and u.payload.lastLoginTime.`value` > ?", new com.blazebit.query.TypeReference<java.util.Map<String, Object>>() {
				} );

			typedQuery.setParameter(1, oneYearAgoMillis);

			assertThat( typedQuery.getResultList() ).hasSize( 1 );
			assertThat( typedQuery.getResultList() ).extracting( result -> result.get( "resourceId" ) ).containsExactly( "active-user-id" );
		}
	}

	@Test
	void should_return_active_users_without_mfa_enrolled() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					GoogleUser.class,
					List.of(
							TestObjects.activeUser(),          // enrolled -> should NOT be returned
							TestObjects.staleUser(),           // enrolled -> should NOT be returned
							TestObjects.suspendedUser(),       // suspended -> should NOT be returned
							TestObjects.activeUserWithoutMfa() // active + no MFA -> should be returned
					)
			);

			var typedQuery =
					session.createQuery(
							"select u.* " +
									"from GoogleUser u " +
									"where u.payload.suspended = false " +
									"and (u.payload.isEnrolledIn2Sv = false or u.payload.isEnrolledIn2Sv is null)",
							new com.blazebit.query.TypeReference<Map<String, Object>>() {}
					);

			var result = typedQuery.getResultList();
			assertThat(result).hasSize(1);
			assertThat(result).extracting(r -> r.get("resourceId")).containsExactly("active-user-no-mfa-id");
		}
	}
}

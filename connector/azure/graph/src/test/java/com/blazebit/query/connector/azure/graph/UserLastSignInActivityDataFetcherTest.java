/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.blazebit.query.QueryContext;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UserLastSignInActivityDataFetcherTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AzureGraphSchemaProvider() );
		builder.registerSchemaObjectAlias( UserLastSignInActivity.class, "UserLastSignInActivity" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_user_last_sign_in_activity() {
		var user = new UserLastSignInActivity( AzureTestObjects.staleEnabledUserWithSignInActivity() );

		try (var session = CONTEXT.createSession()) {
			session.put( UserLastSignInActivity.class, Collections.singletonList( user ) );

			var typedQuery =
					session.createQuery( "select u.* from UserLastSignInActivity u", Map.class );

			assertThat( typedQuery.getResultList() ).first().satisfies( map -> {
				assertThat( map ).containsEntry( "id", user.getId() );
				assertThat( map ).extractingByKey( "signInActivity" ).isNotNull();
			} );
		}
	}

}

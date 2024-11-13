/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.blazebit.query.QueryContext;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import com.microsoft.graph.beta.models.User;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AzureGraphProviderTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AzureGraphSchemaProvider() );
		builder.registerSchemaObjectAlias( UserLastSignInActivity.class, "AzureUserLastSignInActivity" );
		builder.registerSchemaObjectAlias( User.class, "AzureUser" );
		CONTEXT = builder.build();
	}

	@Test
	public void should_return_users() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					User.class, Collections.singletonList( AzureTestObjects.hybridUser() ) );

			var typedQuery =
					session.createQuery( "select u.* from AzureUser u", Map.class );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

}

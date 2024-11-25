/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.blazebit.query.QueryContext;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class UserDataFetcherTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AzureGraphSchemaProvider() );
		builder.registerSchemaObjectAlias( AzureGraphUser.class, "AzureUser" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_users() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					AzureGraphUser.class, Collections.singletonList( AzureTestObjects.hybridUser() ) );

			var typedQuery =
					session.createQuery( "select u.* from AzureUser u" );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

}

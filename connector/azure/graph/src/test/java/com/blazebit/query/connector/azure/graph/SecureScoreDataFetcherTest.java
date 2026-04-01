/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.blazebit.query.QueryContext;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class SecureScoreDataFetcherTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AzureGraphSchemaProvider() );
		builder.registerSchemaObjectAlias( AzureGraphSecureScore.class, "AzureSecureScore" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_all_secure_scores() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureGraphSecureScore.class, List.of(
					AzureTestObjects.secureScoreHigh(), AzureTestObjects.secureScoreLow() ) );

			var typedQuery = session.createQuery( "select s.* from AzureSecureScore s", AzureGraphSecureScore.class );

			assertThat( typedQuery.getResultList() ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_by_tenant_id() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureGraphSecureScore.class, List.of(
					AzureTestObjects.secureScoreHigh(), AzureTestObjects.secureScoreLow() ) );

			var typedQuery = session.createQuery(
					"select s.* from AzureSecureScore s where s.tenantId = '123'",
					AzureGraphSecureScore.class );

			assertThat( typedQuery.getResultList() ).hasSize( 2 );
		}
	}

	@Test
	void secure_score_data_fetcher_is_serializable() throws java.io.IOException {
		var bos = new java.io.ByteArrayOutputStream();
		try ( var oos = new java.io.ObjectOutputStream( bos ) ) {
			oos.writeObject( SecureScoreDataFetcher.INSTANCE );
		}
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RiskyUserDataFetcherTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AzureGraphSchemaProvider() );
		builder.registerSchemaObjectAlias( AzureGraphRiskyUser.class, "AzureRiskyUser" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_all_risky_users() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureGraphRiskyUser.class, List.of(
					AzureTestObjects.riskyUserHigh(),
					AzureTestObjects.riskyUserMedium() ) );

			var result = session.createQuery(
					"select u.tenantId from AzureRiskyUser u",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_by_tenant_id() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureGraphRiskyUser.class, List.of(
					AzureTestObjects.riskyUserHigh(),
					AzureTestObjects.riskyUserMedium() ) );

			var result = session.createQuery(
					"select u.tenantId from AzureRiskyUser u where u.tenantId = 'tenant1'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void risky_user_data_fetcher_is_serializable() throws java.io.IOException {
		var bos = new java.io.ByteArrayOutputStream();
		try ( var oos = new java.io.ObjectOutputStream( bos ) ) {
			oos.writeObject( RiskyUserDataFetcher.INSTANCE );
		}
	}
}

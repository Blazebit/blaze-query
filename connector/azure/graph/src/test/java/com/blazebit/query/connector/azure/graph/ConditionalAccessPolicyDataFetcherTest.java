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

public class ConditionalAccessPolicyDataFetcherTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AzureGraphSchemaProvider() );
		builder.registerSchemaObjectAlias( AzureGraphConditionalAccessPolicy.class, "AzureConditionalAccessPolicy" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_all_policies() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureGraphConditionalAccessPolicy.class, List.of(
					AzureTestObjects.conditionalAccessPolicyEnabled(),
					AzureTestObjects.conditionalAccessPolicyDisabled() ) );

			var result = session.createQuery(
					"select p.tenantId from AzureConditionalAccessPolicy p",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_by_tenant_id() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureGraphConditionalAccessPolicy.class, List.of(
					AzureTestObjects.conditionalAccessPolicyEnabled(),
					AzureTestObjects.conditionalAccessPolicyDisabled() ) );

			var result = session.createQuery(
					"select p.tenantId from AzureConditionalAccessPolicy p where p.tenantId = 'tenant1'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void conditional_access_policy_data_fetcher_is_serializable() throws java.io.IOException {
		var bos = new java.io.ByteArrayOutputStream();
		try ( var oos = new java.io.ObjectOutputStream( bos ) ) {
			oos.writeObject( ConditionalAccessPolicyDataFetcher.INSTANCE );
		}
	}
}

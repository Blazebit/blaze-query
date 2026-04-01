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

public class SecureScoreControlProfileDataFetcherTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AzureGraphSchemaProvider() );
		builder.registerSchemaObjectAlias( AzureGraphSecureScoreControlProfile.class, "AzureSecureScoreControlProfile" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_all_control_profiles() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureGraphSecureScoreControlProfile.class, List.of(
					AzureTestObjects.secureScoreControlProfileIdentity(),
					AzureTestObjects.secureScoreControlProfileApps() ) );

			var typedQuery = session.createQuery(
					"select p.* from AzureSecureScoreControlProfile p",
					AzureGraphSecureScoreControlProfile.class );

			assertThat( typedQuery.getResultList() ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_by_tenant_id() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureGraphSecureScoreControlProfile.class, List.of(
					AzureTestObjects.secureScoreControlProfileIdentity(),
					AzureTestObjects.secureScoreControlProfileApps() ) );

			var typedQuery = session.createQuery(
					"select p.* from AzureSecureScoreControlProfile p where p.tenantId = '123'",
					AzureGraphSecureScoreControlProfile.class );

			assertThat( typedQuery.getResultList() ).hasSize( 2 );
		}
	}

	@Test
	void secure_score_control_profile_data_fetcher_is_serializable() throws java.io.IOException {
		var bos = new java.io.ByteArrayOutputStream();
		try ( var oos = new java.io.ObjectOutputStream( bos ) ) {
			oos.writeObject( SecureScoreControlProfileDataFetcher.INSTANCE );
		}
	}
}

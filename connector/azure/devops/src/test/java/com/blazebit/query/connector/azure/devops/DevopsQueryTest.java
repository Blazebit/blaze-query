/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.devops;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.connector.devops.model.GitRepository;
import com.blazebit.query.connector.devops.model.PolicyConfiguration;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public class DevopsQueryTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new DevopsSchemaProvider() );
		builder.registerSchemaObjectAlias( GitRepository.class, "AzureDevOpsRepository" );
		builder.registerSchemaObjectAlias( PolicyConfiguration.class, "AzureDevOpsPolicyConfiguration" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_repository() {
		try (var session = CONTEXT.createSession()) {
			GitRepository repo = DevopsTestObjects.mainServiceRepository();
			session.put( GitRepository.class, List.of( repo ) );

			var query = session.createQuery(
					"select r.* from AzureDevOpsRepository r",
					new TypeReference<Map<String, Object>>() {} );

			assertThat( query.getResultList() )
					.extracting( row -> row.get( "name" ) )
					.containsExactly( "main-service" );
		}
	}

	@Test
	void should_return_policy_configuration() {
		try (var session = CONTEXT.createSession()) {
			PolicyConfiguration policy = DevopsTestObjects.minReviewersPolicy();
			session.put( PolicyConfiguration.class, List.of( policy ) );

			var query = session.createQuery(
					"select p.* from AzureDevOpsPolicyConfiguration p",
					new TypeReference<Map<String, Object>>() {} );

			assertThat( query.getResultList() )
					.extracting( row -> row.get( "id" ) )
					.containsExactly( 42 );
		}
	}

	@Test
	void should_query_disabled_repositories() {
		try (var session = CONTEXT.createSession()) {
			session.put( GitRepository.class, List.of( DevopsTestObjects.mainServiceRepository() ) );

			var query = session.createQuery(
					"select r.name from AzureDevOpsRepository r where r.isDisabled = true",
					new TypeReference<Map<String, Object>>() {} );

			assertThat( query.getResultList() ).isEmpty();
		}
	}

	@Test
	void should_query_blocking_policies() {
		try (var session = CONTEXT.createSession()) {
			session.put( PolicyConfiguration.class, List.of( DevopsTestObjects.minReviewersPolicy() ) );

			var query = session.createQuery(
					"select p.id, p.isBlocking, p.isEnabled from AzureDevOpsPolicyConfiguration p"
							+ " where p.isBlocking = true and p.isEnabled = true",
					new TypeReference<Map<String, Object>>() {} );

			assertThat( query.getResultList() ).hasSize( 1 );
		}
	}
}

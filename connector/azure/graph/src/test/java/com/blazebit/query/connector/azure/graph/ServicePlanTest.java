/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.blazebit.query.QueryContext;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import com.microsoft.graph.beta.models.ServicePlanInfo;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ServicePlanTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AzureGraphSchemaProvider() );
		builder.registerSchemaObjectAlias( ServicePlan.class, "AzureServicePlan" );
		builder.registerSchemaObjectAlias( ServicePlanInfo.class, "AzureAvailableServicePlan" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_available_service_plan() {
		try (var session = CONTEXT.createSession()) {
			var aadPremium = new ServicePlanInfo();
			aadPremium.setServicePlanId( UUID.fromString("41781fb2-bc02-4b7c-bd55-b576c07bb09d") );
			aadPremium.setServicePlanName( "AAD_PREMIUM" );

			session.put(
					ServicePlanInfo.class, Collections.singletonList( aadPremium ) );

			var typedQuery =
					session.createQuery( "select s.* from AzureAvailableServicePlan s", Map.class );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_service_plan() {
		try (var session = CONTEXT.createSession()) {
			var typedQuery =
					session.createQuery( "select s.* from AzureServicePlan s", Map.class );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_correct_service_plans() {
		try (var session = CONTEXT.createSession()) {
			var aadPremium = new ServicePlanInfo();
			aadPremium.setServicePlanId( UUID.fromString("41781fb2-bc02-4b7c-bd55-b576c07bb09d") );
			aadPremium.setServicePlanName( "AAD_PREMIUM" );

			session.put(
					ServicePlanInfo.class, Collections.singletonList( aadPremium ) );

			var typedQuery =
					session.createQuery( "select s.* from AzureServicePlan s where s.id = ? or s.parentId = ?", Map.class );
			typedQuery.setParameter( 1, aadPremium.getServicePlanId().toString() );
			typedQuery.setParameter( 2, aadPremium.getServicePlanId().toString() );

			assertThat( typedQuery.getResultList() ).size().isEqualTo( 60 );
		}
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.resourcemanager.authorization.fluent.models.RoleAssignmentInner;
import com.azure.resourcemanager.authorization.models.PrincipalType;
import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RoleAssignmentDataFetcherTest {

	private static final String SUBSCRIPTION_ID = "e864bc3e-3581-473d-bc31-757e489cf8fa";

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AzureResourceManagerSchemaProvider() );
		builder.registerSchemaObjectAlias( AzureResourceRoleAssignment.class, "AzureRoleAssignment" );
		CONTEXT = builder.build();
	}

	private static AzureResourceRoleAssignment ownerAtSubscriptionScope() {
		var inner = new RoleAssignmentInner()
				.withPrincipalId( "principal-001" )
				.withPrincipalType( PrincipalType.USER )
				.withRoleDefinitionId( "/subscriptions/" + SUBSCRIPTION_ID
						+ "/providers/Microsoft.Authorization/roleDefinitions/8e3af657-a8ff-443c-a75c-2fe8c4bcb635" );
		return new AzureResourceRoleAssignment( "tenant1", SUBSCRIPTION_ID, null, "ra-001", inner );
	}

	private static AzureResourceRoleAssignment contributorAtResourceGroupScope() {
		var inner = new RoleAssignmentInner()
				.withPrincipalId( "principal-002" )
				.withPrincipalType( PrincipalType.SERVICE_PRINCIPAL )
				.withRoleDefinitionId( "/subscriptions/" + SUBSCRIPTION_ID
						+ "/providers/Microsoft.Authorization/roleDefinitions/b24988ac-6180-42a0-ab88-20f7382dd24c" )
				.withDescription( "Deployment service principal" );
		return new AzureResourceRoleAssignment( "tenant1", SUBSCRIPTION_ID, "rg1", "ra-002", inner );
	}

	@Test
	void should_return_all_role_assignments() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureResourceRoleAssignment.class,
					List.of( ownerAtSubscriptionScope(), contributorAtResourceGroupScope() ) );

			var result = session.createQuery(
					"select r.payload.principalId from AzureRoleAssignment r",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_by_resource_group() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureResourceRoleAssignment.class,
					List.of( ownerAtSubscriptionScope(), contributorAtResourceGroupScope() ) );

			var result = session.createQuery(
					"select r.payload.principalId, r.payload.description from AzureRoleAssignment r"
					+ " where r.resourceGroupName = 'rg1'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "principalId" ) ).isEqualTo( "principal-002" );
		}
	}

	@Test
	void should_filter_by_subscription() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureResourceRoleAssignment.class,
					List.of( ownerAtSubscriptionScope(), contributorAtResourceGroupScope() ) );

			var result = session.createQuery(
					"select r.payload.principalId from AzureRoleAssignment r"
					+ " where r.subscriptionId = '" + SUBSCRIPTION_ID + "'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void role_assignment_data_fetcher_is_serializable() throws java.io.IOException {
		var bos = new java.io.ByteArrayOutputStream();
		try ( var oos = new java.io.ObjectOutputStream( bos ) ) {
			oos.writeObject( RoleAssignmentDataFetcher.INSTANCE );
		}
	}
}

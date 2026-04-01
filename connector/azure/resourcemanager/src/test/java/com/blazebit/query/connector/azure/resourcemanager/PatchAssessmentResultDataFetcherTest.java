/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PatchAssessmentResultDataFetcherTest {

	private static final String VM_RESOURCE_ID =
			"/subscriptions/e864bc3e-3581-473d-bc31-757e489cf8fa/resourcegroups/christian/providers/microsoft.compute/virtualmachines/withoutpasswordenabled";

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AzureResourceManagerSchemaProvider() );
		builder.registerSchemaObjectAlias( AzureResourcePatchAssessmentResult.class, "AzurePatchAssessmentResult" );
		CONTEXT = builder.build();
	}

	private static AzureResourcePatchAssessmentResult vmWithCriticalPatches() {
		return new AzureResourcePatchAssessmentResult(
				"tenant1",
				VM_RESOURCE_ID,
				"e864bc3e-3581-473d-bc31-757e489cf8fa",
				"christian",
				"withoutpasswordenabled",
				"2025-02-04T12:52:45Z",
				"2025-02-04T12:52:20Z",
				"Succeeded",
				false,
				5,
				10,
				2,
				3 );
	}

	private static AzureResourcePatchAssessmentResult vmWithNoPatches() {
		return new AzureResourcePatchAssessmentResult(
				"tenant1",
				"/subscriptions/e864bc3e-3581-473d-bc31-757e489cf8fa/resourcegroups/christian/providers/microsoft.compute/virtualmachines/fullypatched",
				"e864bc3e-3581-473d-bc31-757e489cf8fa",
				"christian",
				"fullypatched",
				"2025-02-04T10:00:00Z",
				"2025-02-04T09:58:00Z",
				"Succeeded",
				false,
				0,
				0,
				0,
				0 );
	}

	@Test
	void should_return_all_assessments() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureResourcePatchAssessmentResult.class,
					List.of( vmWithCriticalPatches(), vmWithNoPatches() ) );

			var result = session.createQuery(
					"select r.vmName from AzurePatchAssessmentResult r",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_vms_with_open_critical_patches() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureResourcePatchAssessmentResult.class,
					List.of( vmWithCriticalPatches(), vmWithNoPatches() ) );

			var result = session.createQuery(
					"select r.vmName, r.criticalAndSecurityPatchCount"
					+ " from AzurePatchAssessmentResult r"
					+ " where r.criticalAndSecurityPatchCount > 0",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "vmName" ) ).isEqualTo( "withoutpasswordenabled" );
		}
	}

	@Test
	void should_filter_by_status_and_critical_patches() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureResourcePatchAssessmentResult.class,
					List.of( vmWithCriticalPatches(), vmWithNoPatches() ) );

			var result = session.createQuery(
					"select r.vmName, r.criticalPatchCount, r.securityPatchCount"
					+ " from AzurePatchAssessmentResult r"
					+ " where r.statusCode = 'Succeeded'"
					+ " and (r.criticalPatchCount > 0 or r.securityPatchCount > 0)",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "criticalPatchCount" ) ).isEqualTo( 2 );
			assertThat( result.get( 0 ).get( "securityPatchCount" ) ).isEqualTo( 3 );
		}
	}

	@Test
	void should_filter_by_subscription() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureResourcePatchAssessmentResult.class,
					List.of( vmWithCriticalPatches(), vmWithNoPatches() ) );

			var result = session.createQuery(
					"select r.vmName from AzurePatchAssessmentResult r"
					+ " where r.subscriptionId = 'e864bc3e-3581-473d-bc31-757e489cf8fa'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void patch_assessment_result_data_fetcher_is_serializable() throws java.io.IOException {
		var bos = new java.io.ByteArrayOutputStream();
		try ( var oos = new java.io.ObjectOutputStream( bos ) ) {
			oos.writeObject( PatchAssessmentResultDataFetcher.INSTANCE );
		}
	}
}

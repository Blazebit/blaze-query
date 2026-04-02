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

public class SecurityAssessmentDataFetcherTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AzureResourceManagerSchemaProvider() );
		builder.registerSchemaObjectAlias( AzureResourceSecurityAssessment.class, "AzureSecurityAssessment" );
		CONTEXT = builder.build();
	}

	private static AzureResourceSecurityAssessment unhealthyHighAssessment() {
		return new AzureResourceSecurityAssessment(
				"tenant1",
				"/subscriptions/e864bc3e-3581-473d-bc31-757e489cf8fa/resourceGroups/rg1"
				+ "/providers/microsoft.compute/virtualmachines/vm1"
				+ "/providers/microsoft.security/assessments/550a99d4-a129-4a4d-8986-4f3d02b4f69c",
				"e864bc3e-3581-473d-bc31-757e489cf8fa",
				"/subscriptions/e864bc3e-3581-473d-bc31-757e489cf8fa/resourcegroups/rg1/providers/microsoft.compute/virtualmachines/vm1",
				"Machines should have vulnerability findings resolved",
				"Unhealthy",
				"High" );
	}

	private static AzureResourceSecurityAssessment healthyMediumAssessment() {
		return new AzureResourceSecurityAssessment(
				"tenant1",
				"/subscriptions/e864bc3e-3581-473d-bc31-757e489cf8fa/resourceGroups/rg1"
				+ "/providers/microsoft.compute/virtualmachines/vm2"
				+ "/providers/microsoft.security/assessments/550a99d4-a129-4a4d-8986-4f3d02b4f69c",
				"e864bc3e-3581-473d-bc31-757e489cf8fa",
				"/subscriptions/e864bc3e-3581-473d-bc31-757e489cf8fa/resourcegroups/rg1/providers/microsoft.compute/virtualmachines/vm2",
				"Machines should have vulnerability findings resolved",
				"Healthy",
				"Medium" );
	}

	@Test
	void should_return_all_assessments() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureResourceSecurityAssessment.class,
					List.of( unhealthyHighAssessment(), healthyMediumAssessment() ) );

			var result = session.createQuery(
					"select a.displayName from AzureSecurityAssessment a",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_unhealthy_assessments() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureResourceSecurityAssessment.class,
					List.of( unhealthyHighAssessment(), healthyMediumAssessment() ) );

			var result = session.createQuery(
					"select a.displayName, a.severity from AzureSecurityAssessment a"
					+ " where a.statusCode = 'Unhealthy'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "severity" ) ).isEqualTo( "High" );
		}
	}

	@Test
	void should_filter_by_severity() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureResourceSecurityAssessment.class,
					List.of( unhealthyHighAssessment(), healthyMediumAssessment() ) );

			var result = session.createQuery(
					"select a.statusCode from AzureSecurityAssessment a where a.severity = 'High'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "statusCode" ) ).isEqualTo( "Unhealthy" );
		}
	}

	@Test
	void should_filter_by_subscription() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( AzureResourceSecurityAssessment.class,
					List.of( unhealthyHighAssessment(), healthyMediumAssessment() ) );

			var result = session.createQuery(
					"select a.displayName from AzureSecurityAssessment a"
					+ " where a.subscriptionId = 'e864bc3e-3581-473d-bc31-757e489cf8fa'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void security_assessment_data_fetcher_is_serializable() throws java.io.IOException {
		var bos = new java.io.ByteArrayOutputStream();
		try ( var oos = new java.io.ObjectOutputStream( bos ) ) {
			oos.writeObject( SecurityAssessmentDataFetcher.INSTANCE );
		}
	}
}

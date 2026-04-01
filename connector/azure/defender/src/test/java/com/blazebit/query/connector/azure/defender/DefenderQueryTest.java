/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.defender;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DefenderQueryTest {

	private static final QueryContext CONTEXT;
	private static final ObjectMapper MAPPER = new ObjectMapper();

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new DefenderSchemaProvider() );
		builder.registerSchemaObjectAlias( DefenderMachine.class, "DefenderMachine" );
		builder.registerSchemaObjectAlias( DefenderAlert.class, "DefenderAlert" );
		builder.registerSchemaObjectAlias( DefenderVulnerability.class, "DefenderVulnerability" );
		builder.registerSchemaObjectAlias( DefenderRecommendation.class, "DefenderRecommendation" );
		CONTEXT = builder.build();
	}

	private static DefenderMachine windowsMachine() throws Exception {
		return DefenderMachine.fromJson( "tenant1", MAPPER.readTree(
				"{\"id\":\"machine1\",\"computerDnsName\":\"win-host\",\"osPlatform\":\"Windows10\"," +
				"\"riskScore\":\"High\",\"exposureLevel\":\"High\",\"status\":\"Active\"," +
				"\"onboardingStatus\":\"Onboarded\",\"isAadJoined\":true}" ) );
	}

	private static DefenderMachine linuxMachine() throws Exception {
		return DefenderMachine.fromJson( "tenant1", MAPPER.readTree(
				"{\"id\":\"machine2\",\"computerDnsName\":\"linux-host\",\"osPlatform\":\"Linux\"," +
				"\"riskScore\":\"Low\",\"exposureLevel\":\"Low\",\"status\":\"Active\"," +
				"\"onboardingStatus\":\"Onboarded\",\"isAadJoined\":false}" ) );
	}

	private static DefenderAlert highAlert() throws Exception {
		return DefenderAlert.fromJson( "tenant1", MAPPER.readTree(
				"{\"id\":\"alert1\",\"title\":\"Suspicious process\",\"severity\":\"High\"," +
				"\"status\":\"New\",\"category\":\"Malware\",\"machineId\":\"machine1\"," +
				"\"alertCreationTime\":\"2025-01-01T00:00:00Z\"}" ) );
	}

	private static DefenderAlert lowAlert() throws Exception {
		return DefenderAlert.fromJson( "tenant1", MAPPER.readTree(
				"{\"id\":\"alert2\",\"title\":\"Minor anomaly\",\"severity\":\"Low\"," +
				"\"status\":\"Resolved\",\"category\":\"General\",\"machineId\":\"machine2\"," +
				"\"alertCreationTime\":\"2025-01-02T00:00:00Z\"}" ) );
	}

	private static DefenderVulnerability criticalVuln() throws Exception {
		return DefenderVulnerability.fromJson( MAPPER.readTree(
				"{\"id\":\"CVE-2024-0001\",\"name\":\"Critical Vuln\",\"severity\":\"Critical\"," +
				"\"cvssV3\":9.8,\"exposedMachines\":10,\"publicExploit\":true," +
				"\"exploitVerified\":true}" ) );
	}

	private static DefenderVulnerability mediumVuln() throws Exception {
		return DefenderVulnerability.fromJson( MAPPER.readTree(
				"{\"id\":\"CVE-2024-0002\",\"name\":\"Medium Vuln\",\"severity\":\"Medium\"," +
				"\"cvssV3\":5.5,\"exposedMachines\":3,\"publicExploit\":false}" ) );
	}

	private static DefenderRecommendation activeRec() throws Exception {
		return DefenderRecommendation.fromJson( MAPPER.readTree(
				"{\"id\":\"rec1\",\"productName\":\"Windows\",\"recommendationName\":\"Update OS\"," +
				"\"status\":\"Active\",\"remediationType\":\"Update\",\"severityScore\":8.0," +
				"\"exposedMachinesCount\":5}" ) );
	}

	private static DefenderRecommendation completedRec() throws Exception {
		return DefenderRecommendation.fromJson( MAPPER.readTree(
				"{\"id\":\"rec2\",\"productName\":\"Chrome\",\"recommendationName\":\"Update browser\"," +
				"\"status\":\"Completed\",\"remediationType\":\"Update\",\"severityScore\":4.0," +
				"\"exposedMachinesCount\":0}" ) );
	}

	@Test
	void should_return_all_machines() throws Exception {
		try ( var session = CONTEXT.createSession() ) {
			session.put( DefenderMachine.class, List.of( windowsMachine(), linuxMachine() ) );

			var result = session.createQuery( "select m.id from DefenderMachine m",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_windows_machines() throws Exception {
		try ( var session = CONTEXT.createSession() ) {
			session.put( DefenderMachine.class, List.of( windowsMachine(), linuxMachine() ) );

			var result = session.createQuery(
					"select m.id from DefenderMachine m where m.osPlatform = 'Windows10'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "id" ) ).isEqualTo( "machine1" );
		}
	}

	@Test
	void should_return_all_alerts() throws Exception {
		try ( var session = CONTEXT.createSession() ) {
			session.put( DefenderAlert.class, List.of( highAlert(), lowAlert() ) );

			var result = session.createQuery( "select a.id from DefenderAlert a",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_high_severity_alerts() throws Exception {
		try ( var session = CONTEXT.createSession() ) {
			session.put( DefenderAlert.class, List.of( highAlert(), lowAlert() ) );

			var result = session.createQuery(
					"select a.id from DefenderAlert a where a.severity = 'High'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "id" ) ).isEqualTo( "alert1" );
		}
	}

	@Test
	void should_return_all_vulnerabilities() throws Exception {
		try ( var session = CONTEXT.createSession() ) {
			session.put( DefenderVulnerability.class, List.of( criticalVuln(), mediumVuln() ) );

			var result = session.createQuery( "select v.id from DefenderVulnerability v",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_critical_vulnerabilities() throws Exception {
		try ( var session = CONTEXT.createSession() ) {
			session.put( DefenderVulnerability.class, List.of( criticalVuln(), mediumVuln() ) );

			var result = session.createQuery(
					"select v.id from DefenderVulnerability v where v.severity = 'Critical'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "id" ) ).isEqualTo( "CVE-2024-0001" );
		}
	}

	@Test
	void should_return_all_recommendations() throws Exception {
		try ( var session = CONTEXT.createSession() ) {
			session.put( DefenderRecommendation.class, List.of( activeRec(), completedRec() ) );

			var result = session.createQuery( "select r.id from DefenderRecommendation r",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_active_recommendations() throws Exception {
		try ( var session = CONTEXT.createSession() ) {
			session.put( DefenderRecommendation.class, List.of( activeRec(), completedRec() ) );

			var result = session.createQuery(
					"select r.id from DefenderRecommendation r where r.status = 'Active'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "id" ) ).isEqualTo( "rec1" );
		}
	}
}

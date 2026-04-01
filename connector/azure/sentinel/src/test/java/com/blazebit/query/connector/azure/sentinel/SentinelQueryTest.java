/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.sentinel;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SentinelQueryTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new SentinelSchemaProvider() );
		builder.registerSchemaObjectAlias( SentinelIncident.class, "SentinelIncident" );
		builder.registerSchemaObjectAlias( SentinelAlertRule.class, "SentinelAlertRule" );
		builder.registerSchemaObjectAlias( SentinelDataConnector.class, "SentinelDataConnector" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_all_incidents() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( SentinelIncident.class, List.of(
					SentinelTestObjects.incidentHigh(), SentinelTestObjects.incidentLow() ) );

			var result = session.createQuery( "select i.tenantId from SentinelIncident i",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_incidents_by_workspace() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( SentinelIncident.class, List.of(
					SentinelTestObjects.incidentHigh(), SentinelTestObjects.incidentLow() ) );

			var result = session.createQuery(
					"select i.tenantId from SentinelIncident i where i.workspaceName = 'ws1'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_return_all_alert_rules() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( SentinelAlertRule.class, List.of( SentinelTestObjects.alertRule() ) );

			var result = session.createQuery( "select r.tenantId from SentinelAlertRule r",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
		}
	}

	@Test
	void should_return_all_data_connectors() {
		try ( var session = CONTEXT.createSession() ) {
			session.put( SentinelDataConnector.class, List.of( SentinelTestObjects.dataConnector() ) );

			var result = session.createQuery( "select c.tenantId from SentinelDataConnector c",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
		}
	}

	@Test
	void sentinel_incident_data_fetcher_is_serializable() throws java.io.IOException {
		var bos = new java.io.ByteArrayOutputStream();
		try ( var oos = new java.io.ObjectOutputStream( bos ) ) {
			oos.writeObject( SentinelIncidentDataFetcher.INSTANCE );
		}
	}
}

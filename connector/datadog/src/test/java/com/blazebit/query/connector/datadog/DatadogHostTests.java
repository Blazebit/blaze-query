/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DatadogHostTests {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new DatadogSchemaProvider() );
		builder.registerSchemaObjectAlias( DatadogHost.class, "DatadogHost" );
		CONTEXT = builder.build();
	}

	private static DatadogHost upHost() {
		return new DatadogHost(
				10001L,
				"web-server-01",
				"web-server-01.prod.internal",
				List.of( "web01" ),
				List.of( "system", "nginx" ),
				List.of( "agent", "aws" ),
				1742389200L,
				true,
				false
		);
	}

	private static DatadogHost downHost() {
		return new DatadogHost(
				10002L,
				"db-server-01",
				"db-server-01.prod.internal",
				List.of( "db01" ),
				List.of( "system", "postgres" ),
				List.of( "agent" ),
				1742302800L,
				false,
				false
		);
	}

	private static DatadogHost mutedHost() {
		return new DatadogHost(
				10003L,
				"staging-server-01",
				"staging-server-01.internal",
				List.of(),
				List.of( "system" ),
				List.of( "agent" ),
				1742389200L,
				true,
				true
		);
	}

	@Test
	void should_return_all_hosts() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogHost.class, List.of( upHost(), downHost(), mutedHost() ) );

			var result = session.createQuery(
					"SELECT h.id, h.name, h.up FROM DatadogHost h",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 3 );
		}
	}

	@Test
	void should_find_up_hosts() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogHost.class, List.of( upHost(), downHost(), mutedHost() ) );

			var result = session.createQuery(
					"SELECT h.name, h.hostName FROM DatadogHost h WHERE h.up = true",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_find_down_hosts() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogHost.class, List.of( upHost(), downHost(), mutedHost() ) );

			var result = session.createQuery(
					"SELECT h.name FROM DatadogHost h WHERE h.up = false",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "name" ) ).isEqualTo( "db-server-01" );
		}
	}

	@Test
	void should_find_active_unmuted_hosts() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogHost.class, List.of( upHost(), downHost(), mutedHost() ) );

			var result = session.createQuery(
					"SELECT h.name, h.hostName FROM DatadogHost h WHERE h.up = true AND h.muted = false",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "name" ) ).isEqualTo( "web-server-01" );
		}
	}
}

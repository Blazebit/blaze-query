/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.datadog;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DatadogMonitorTests {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new DatadogSchemaProvider() );
		builder.registerSchemaObjectAlias( DatadogMonitor.class, "DatadogMonitor" );
		CONTEXT = builder.build();
	}

	private static DatadogMonitor alertingMonitor() {
		return new DatadogMonitor(
				1001L,
				"High CPU usage on prod hosts",
				"metric alert",
				"Alert",
				1L,
				"avg(last_5m):avg:system.cpu.user{env:prod} > 90",
				"CPU usage exceeded 90% threshold",
				List.of( "env:prod", "team:infra" ),
				OffsetDateTime.parse( "2026-01-01T00:00:00Z" ),
				OffsetDateTime.parse( "2026-03-01T00:00:00Z" )
		);
	}

	private static DatadogMonitor okMonitor() {
		return new DatadogMonitor(
				1002L,
				"Disk space check",
				"metric alert",
				"OK",
				2L,
				"avg(last_5m):avg:system.disk.in_use{env:prod} > 0.8",
				"Disk usage exceeded 80% threshold",
				List.of( "env:prod", "team:infra" ),
				OffsetDateTime.parse( "2026-01-15T00:00:00Z" ),
				OffsetDateTime.parse( "2026-03-10T00:00:00Z" )
		);
	}

	@Test
	void should_return_all_monitors() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogMonitor.class, List.of( alertingMonitor(), okMonitor() ) );

			var result = session.createQuery(
					"SELECT m.id, m.name, m.overallState FROM DatadogMonitor m",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_find_alerting_monitors() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogMonitor.class, List.of( alertingMonitor(), okMonitor() ) );

			var result = session.createQuery(
					"SELECT m.id, m.name FROM DatadogMonitor m WHERE m.overallState = 'Alert'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "name" ) ).isEqualTo( "High CPU usage on prod hosts" );
		}
	}

	@Test
	void should_find_healthy_monitors() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogMonitor.class, List.of( alertingMonitor(), okMonitor() ) );

			var result = session.createQuery(
					"SELECT m.id, m.name FROM DatadogMonitor m WHERE m.overallState = 'OK'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "name" ) ).isEqualTo( "Disk space check" );
		}
	}

	@Test
	void should_filter_by_priority() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogMonitor.class, List.of( alertingMonitor(), okMonitor() ) );

			var result = session.createQuery(
					"SELECT m.id, m.name, m.overallState FROM DatadogMonitor m WHERE m.priority = 1",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "overallState" ) ).isEqualTo( "Alert" );
		}
	}
}

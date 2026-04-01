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

public class DatadogLogTests {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new DatadogSchemaProvider() );
		builder.registerSchemaObjectAlias( DatadogLog.class, "DatadogLog" );
		CONTEXT = builder.build();
	}

	private static DatadogLog errorLog() {
		return new DatadogLog(
				"log-001",
				"web-server-01",
				"web-api",
				"error",
				"NullPointerException in OrderService",
				OffsetDateTime.parse( "2026-03-19T10:00:00Z" ),
				List.of( "env:prod", "team:backend" )
		);
	}

	private static DatadogLog infoLog() {
		return new DatadogLog(
				"log-002",
				"web-server-02",
				"web-api",
				"info",
				"Request processed successfully",
				OffsetDateTime.parse( "2026-03-19T10:01:00Z" ),
				List.of( "env:prod", "team:backend" )
		);
	}

	@Test
	void should_return_all_logs() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogLog.class, List.of( errorLog(), infoLog() ) );

			var result = session.createQuery(
					"SELECT l.id, l.service, l.status FROM DatadogLog l",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_filter_error_logs() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogLog.class, List.of( errorLog(), infoLog() ) );

			var result = session.createQuery(
					"SELECT l.id, l.host, l.message FROM DatadogLog l WHERE l.status = 'error'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "host" ) ).isEqualTo( "web-server-01" );
		}
	}

	@Test
	void should_filter_logs_by_service() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogLog.class, List.of( errorLog(), infoLog() ) );

			var result = session.createQuery(
					"SELECT l.id, l.status FROM DatadogLog l WHERE l.service = 'web-api'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_count_errors_per_host() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogLog.class, List.of( errorLog(), infoLog() ) );

			var result = session.createQuery(
					"SELECT l.host, COUNT(*) AS error_count FROM DatadogLog l WHERE l.status = 'error' GROUP BY l.host",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "error_count" ) ).isEqualTo( 1L );
		}
	}
}

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

public class DatadogSyntheticsTestTests {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new DatadogSchemaProvider() );
		builder.registerSchemaObjectAlias( DatadogSyntheticsTest.class, "DatadogSyntheticsTest" );
		CONTEXT = builder.build();
	}

	private static DatadogSyntheticsTest liveApiTest() {
		return new DatadogSyntheticsTest(
				"abc-123-def",
				"Homepage uptime check",
				"api",
				"live",
				List.of( "env:prod", "team:platform" ),
				List.of( "aws:us-east-1", "aws:eu-west-1" )
		);
	}

	private static DatadogSyntheticsTest pausedBrowserTest() {
		return new DatadogSyntheticsTest(
				"xyz-456-uvw",
				"Login flow browser test",
				"browser",
				"paused",
				List.of( "env:staging" ),
				List.of( "aws:us-east-1" )
		);
	}

	@Test
	void should_return_all_tests() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogSyntheticsTest.class, List.of( liveApiTest(), pausedBrowserTest() ) );

			var result = session.createQuery(
					"SELECT t.publicId, t.name, t.type, t.status FROM DatadogSyntheticsTest t",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_find_live_tests() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogSyntheticsTest.class, List.of( liveApiTest(), pausedBrowserTest() ) );

			var result = session.createQuery(
					"SELECT t.publicId, t.name FROM DatadogSyntheticsTest t WHERE t.status = 'live'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "name" ) ).isEqualTo( "Homepage uptime check" );
		}
	}

	@Test
	void should_find_paused_tests() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogSyntheticsTest.class, List.of( liveApiTest(), pausedBrowserTest() ) );

			var result = session.createQuery(
					"SELECT t.publicId, t.name FROM DatadogSyntheticsTest t WHERE t.status = 'paused'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result.get( 0 ).get( "name" ) ).isEqualTo( "Login flow browser test" );
		}
	}

	@Test
	void should_filter_by_type() {
		try (var session = CONTEXT.createSession()) {
			session.put( DatadogSyntheticsTest.class, List.of( liveApiTest(), pausedBrowserTest() ) );

			var result = session.createQuery(
					"SELECT t.publicId FROM DatadogSyntheticsTest t WHERE t.type = 'api'",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 1 );
		}
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.dns;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import com.google.cloud.dns.ZoneInfo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GcpDnsManagedZoneTests {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new GcpDnsSchemaProvider() );
		builder.registerSchemaObjectAlias( GcpDnsManagedZone.class, "GcpDnsManagedZone" );
		CONTEXT = builder.build();
	}

	private static GcpDnsManagedZone zoneWithDnssec() {
		ZoneInfo zone = ZoneInfo.newBuilder( "secure-zone" )
				.setDnsName( "example.com." )
				.setDnsSecConfig( ZoneInfo.DnsSecConfig.newBuilder()
						.setState( "on" )
						.build() )
				.build();
		return new GcpDnsManagedZone( "secure-zone", zone );
	}

	private static GcpDnsManagedZone zoneWithoutDnssec() {
		ZoneInfo zone = ZoneInfo.newBuilder( "insecure-zone" )
				.setDnsName( "insecure.example.com." )
				.setDnsSecConfig( ZoneInfo.DnsSecConfig.newBuilder()
						.setState( "off" )
						.build() )
				.build();
		return new GcpDnsManagedZone( "insecure-zone", zone );
	}

	@Test
	void should_return_all_managed_zones() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpDnsManagedZone.class, List.of( zoneWithDnssec(), zoneWithoutDnssec() ) );

			var result = session.createQuery(
					"SELECT z.resourceId, z.payload.name FROM GcpDnsManagedZone z",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_detect_dnssec_enabled() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpDnsManagedZone.class, List.of( zoneWithDnssec(), zoneWithoutDnssec() ) );

			var result = session.createQuery(
					"""
					SELECT z.resourceId,
						z.payload.name,
						z.payload.dnsSecConfig.state = 'on' AS passed
					FROM GcpDnsManagedZone z
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( true, false );
		}
	}

	@Test
	void should_return_zone_dns_name() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpDnsManagedZone.class, List.of( zoneWithDnssec(), zoneWithoutDnssec() ) );

			var result = session.createQuery(
					"""
					SELECT z.resourceId,
						z.payload.dnsName
					FROM GcpDnsManagedZone z
					ORDER BY z.payload.name
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> (String) r.get( "dnsName" ) )
					.containsExactlyInAnyOrder( "example.com.", "insecure.example.com." );
		}
	}
}

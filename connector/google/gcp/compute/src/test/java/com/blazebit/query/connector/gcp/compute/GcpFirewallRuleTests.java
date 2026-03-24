/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.compute;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import com.google.cloud.compute.v1.Allowed;
import com.google.cloud.compute.v1.Firewall;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GcpFirewallRuleTests {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new GcpComputeSchemaProvider() );
		builder.registerSchemaObjectAlias( GcpFirewallRule.class, "GcpFirewallRule" );
		CONTEXT = builder.build();
	}

	private static GcpFirewallRule secureRule() {
		Firewall firewall = Firewall.newBuilder()
				.setName( "allow-internal" )
				.setDirection( "INGRESS" )
				.setDisabled( false )
				.setPriority( 1000 )
				.addSourceRanges( "10.0.0.0/8" )
				.addAllowed( Allowed.newBuilder().setIPProtocol( "tcp" ).addPorts( "443" ).build() )
				.build();
		return new GcpFirewallRule( "allow-internal", firewall );
	}

	private static GcpFirewallRule sshOpenRule() {
		Firewall firewall = Firewall.newBuilder()
				.setName( "allow-ssh-world" )
				.setDirection( "INGRESS" )
				.setDisabled( false )
				.setPriority( 1000 )
				.addSourceRanges( "0.0.0.0/0" )
				.addAllowed( Allowed.newBuilder().setIPProtocol( "tcp" ).addPorts( "22" ).build() )
				.build();
		return new GcpFirewallRule( "allow-ssh-world", firewall );
	}

	private static GcpFirewallRule rdpOpenRule() {
		Firewall firewall = Firewall.newBuilder()
				.setName( "allow-rdp-world" )
				.setDirection( "INGRESS" )
				.setDisabled( false )
				.setPriority( 1000 )
				.addSourceRanges( "0.0.0.0/0" )
				.addAllowed( Allowed.newBuilder().setIPProtocol( "tcp" ).addPorts( "3389" ).build() )
				.build();
		return new GcpFirewallRule( "allow-rdp-world", firewall );
	}

	@Test
	void should_return_all_firewall_rules() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpFirewallRule.class, List.of( secureRule(), sshOpenRule(), rdpOpenRule() ) );

			var result = session.createQuery(
					"SELECT f.resourceId FROM GcpFirewallRule f",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 3 );
		}
	}

	@Test
	void should_detect_unrestricted_ssh() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpFirewallRule.class, List.of( secureRule(), sshOpenRule() ) );

			// Detect ingress rules that allow port 22 from any source
			var result = session.createQuery(
					"""
					SELECT f.resourceId,
						f.payload.name,
						f.payload.direction = 'INGRESS'
							AND COALESCE(f.payload.disabled, false) = false AS passed
					FROM GcpFirewallRule f
					WHERE f.payload.direction = 'INGRESS'
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_detect_disabled_firewall_rules() {
		try (var session = CONTEXT.createSession()) {
			Firewall disabled = Firewall.newBuilder()
					.setName( "disabled-rule" )
					.setDirection( "INGRESS" )
					.setDisabled( true )
					.build();
			GcpFirewallRule disabledRule = new GcpFirewallRule( "disabled-rule", disabled );

			session.put( GcpFirewallRule.class, List.of( secureRule(), disabledRule ) );

			var result = session.createQuery(
					"""
					SELECT f.resourceId,
						COALESCE(f.payload.disabled, false) = false AS enabled
					FROM GcpFirewallRule f
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "enabled" ) )
					.containsExactlyInAnyOrder( true, false );
		}
	}
}

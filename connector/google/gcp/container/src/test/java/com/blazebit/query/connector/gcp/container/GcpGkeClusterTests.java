/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.container;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import com.google.container.v1.Cluster;
import com.google.container.v1.MasterAuthorizedNetworksConfig;
import com.google.container.v1.NetworkPolicy;
import com.google.container.v1.NodePool;
import com.google.container.v1.NodeConfig;
import com.google.container.v1.ShieldedInstanceConfig;
import com.google.container.v1.WorkloadIdentityConfig;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GcpGkeClusterTests {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new GcpContainerSchemaProvider() );
		builder.registerSchemaObjectAlias( GcpGkeCluster.class, "GcpGkeCluster" );
		CONTEXT = builder.build();
	}

	private static GcpGkeCluster secureCluster() {
		Cluster cluster = Cluster.newBuilder()
				.setName( "secure-cluster" )
				.setLocation( "europe-west1" )
				.setNetworkPolicy( NetworkPolicy.newBuilder().setEnabled( true ).build() )
				.setMasterAuthorizedNetworksConfig( MasterAuthorizedNetworksConfig.newBuilder()
						.setEnabled( true )
						.build() )
				.setWorkloadIdentityConfig( WorkloadIdentityConfig.newBuilder()
						.setWorkloadPool( "my-project.svc.id.goog" )
						.build() )
				.addNodePools( NodePool.newBuilder()
						.setName( "default-pool" )
						.setConfig( NodeConfig.newBuilder()
								.setShieldedInstanceConfig( ShieldedInstanceConfig.newBuilder()
										.setEnableSecureBoot( true )
										.setEnableIntegrityMonitoring( true )
										.build() )
								.build() )
						.build() )
				.build();
		return new GcpGkeCluster( "secure-cluster", cluster );
	}

	private static GcpGkeCluster insecureCluster() {
		Cluster cluster = Cluster.newBuilder()
				.setName( "insecure-cluster" )
				.setLocation( "us-central1" )
				.setNetworkPolicy( NetworkPolicy.newBuilder().setEnabled( false ).build() )
				.setMasterAuthorizedNetworksConfig( MasterAuthorizedNetworksConfig.newBuilder()
						.setEnabled( false )
						.build() )
				.build();
		return new GcpGkeCluster( "insecure-cluster", cluster );
	}

	@Test
	void should_return_all_clusters() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpGkeCluster.class, List.of( secureCluster(), insecureCluster() ) );

			var result = session.createQuery(
					"SELECT c.resourceId, c.payload.name FROM GcpGkeCluster c",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_detect_network_policy_enabled() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpGkeCluster.class, List.of( secureCluster(), insecureCluster() ) );

			var result = session.createQuery(
					"""
					SELECT c.resourceId,
						c.payload.name,
						COALESCE(c.payload.networkPolicy.enabled, false) = true AS passed
					FROM GcpGkeCluster c
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( true, false );
		}
	}

	@Test
	void should_detect_master_authorized_networks_enabled() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpGkeCluster.class, List.of( secureCluster(), insecureCluster() ) );

			var result = session.createQuery(
					"""
					SELECT c.resourceId,
						c.payload.name,
						COALESCE(c.payload.masterAuthorizedNetworksConfig.enabled, false) = true AS passed
					FROM GcpGkeCluster c
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( true, false );
		}
	}

	@Test
	void should_detect_workload_identity_configured() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpGkeCluster.class, List.of( secureCluster(), insecureCluster() ) );

			var result = session.createQuery(
					"""
					SELECT c.resourceId,
						c.payload.name,
						c.payload.workloadIdentityConfig.workloadPool IS NOT NULL
							AND c.payload.workloadIdentityConfig.workloadPool <> '' AS passed
					FROM GcpGkeCluster c
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( true, false );
		}
	}
}

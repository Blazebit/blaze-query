/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.connector.gcp.compute.GcpComputeSchemaProvider;
import com.blazebit.query.connector.gcp.compute.GcpInstance;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import com.google.cloud.compute.v1.ConfidentialInstanceConfig;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.ShieldedInstanceConfig;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GcpInstanceTests {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new GcpComputeSchemaProvider() );
		builder.registerSchemaObjectAlias( GcpInstance.class, "GcpInstance" );
		CONTEXT = builder.build();
	}

	private static GcpInstance secureInstance() {
		Instance instance = Instance.newBuilder()
				.setName( "secure-instance" )
				.setId( 123456 )
				.setCanIpForward( false )
				.setDeletionProtection( true )
				.setShieldedInstanceConfig( ShieldedInstanceConfig.newBuilder()
						.setEnableSecureBoot( true )
						.setEnableVtpm( true )
						.setEnableIntegrityMonitoring( true )
						.build() )
				.setConfidentialInstanceConfig( ConfidentialInstanceConfig.newBuilder()
						.setEnableConfidentialCompute( true )
						.build() )
				.build();
		return new GcpInstance( "123456", instance );
	}

	private static GcpInstance insecureInstance() {
		Instance instance = Instance.newBuilder()
				.setName( "insecure-instance" )
				.setId( 789012 )
				.setCanIpForward( true )
				.setDeletionProtection( false )
				.setShieldedInstanceConfig( ShieldedInstanceConfig.newBuilder()
						.setEnableSecureBoot( false )
						.setEnableVtpm( false )
						.setEnableIntegrityMonitoring( false )
						.build() )
				.build();
		return new GcpInstance( "789012", instance );
	}

	@Test
	void should_return_all_instances() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpInstance.class, List.of( secureInstance(), insecureInstance() ) );

			var result = session.createQuery(
					"SELECT i.resourceId, i.payload.name FROM GcpInstance i",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_detect_shielded_vm_enabled() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpInstance.class, List.of( secureInstance(), insecureInstance() ) );

			var result = session.createQuery(
					"""
					SELECT i.resourceId,
						i.payload.name,
						COALESCE(i.payload.shieldedInstanceConfig.enableSecureBoot, false)
							AND COALESCE(i.payload.shieldedInstanceConfig.enableVtpm, false)
							AND COALESCE(i.payload.shieldedInstanceConfig.enableIntegrityMonitoring, false) AS passed
					FROM GcpInstance i
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( true, false );
		}
	}

	@Test
	void should_detect_ip_forwarding_disabled() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpInstance.class, List.of( secureInstance(), insecureInstance() ) );

			var result = session.createQuery(
					"""
					SELECT i.resourceId,
						i.payload.name,
						COALESCE(i.payload.canIpForward, false) = false AS passed
					FROM GcpInstance i
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( true, false );
		}
	}

	@Test
	void should_detect_deletion_protection_enabled() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpInstance.class, List.of( secureInstance(), insecureInstance() ) );

			var result = session.createQuery(
					"""
					SELECT i.resourceId,
						i.payload.name,
						COALESCE(i.payload.deletionProtection, false) = true AS passed
					FROM GcpInstance i
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( true, false );
		}
	}

	@Test
	void should_detect_confidential_computing_enabled() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpInstance.class, List.of( secureInstance(), insecureInstance() ) );

			var result = session.createQuery(
					"""
					SELECT i.resourceId,
						i.payload.name,
						COALESCE(i.payload.confidentialInstanceConfig.enableConfidentialCompute, false) = true AS passed
					FROM GcpInstance i
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( true, false );
		}
	}
}

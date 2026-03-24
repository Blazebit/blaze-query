/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.sql;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import com.google.api.services.sqladmin.model.BackupConfiguration;
import com.google.api.services.sqladmin.model.DatabaseInstance;
import com.google.api.services.sqladmin.model.IpConfiguration;
import com.google.api.services.sqladmin.model.Settings;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GcpSqlInstanceTests {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new GcpSqlSchemaProvider() );
		builder.registerSchemaObjectAlias( GcpSqlInstance.class, "GcpSqlInstance" );
		CONTEXT = builder.build();
	}

	private static GcpSqlInstance secureInstance() {
		DatabaseInstance instance = new DatabaseInstance();
		instance.setName( "secure-sql" );
		instance.setRegion( "europe-west1" );
		instance.setDatabaseVersion( "POSTGRES_15" );
		IpConfiguration ipConfig = new IpConfiguration();
		ipConfig.setIpv4Enabled( false );
		ipConfig.setSslMode( "TRUSTED_CLIENT_CERTIFICATE_REQUIRED" );
		BackupConfiguration backupConfig = new BackupConfiguration();
		backupConfig.setEnabled( true );
		Settings settings = new Settings();
		settings.setIpConfiguration( ipConfig );
		settings.setBackupConfiguration( backupConfig );
		instance.setSettings( settings );
		return new GcpSqlInstance( "secure-sql", instance );
	}

	private static GcpSqlInstance insecureInstance() {
		DatabaseInstance instance = new DatabaseInstance();
		instance.setName( "insecure-sql" );
		instance.setRegion( "us-central1" );
		instance.setDatabaseVersion( "MYSQL_8_0" );
		IpConfiguration ipConfig = new IpConfiguration();
		ipConfig.setIpv4Enabled( true );
		ipConfig.setSslMode( "ALLOW_UNENCRYPTED_AND_ENCRYPTED" );
		BackupConfiguration backupConfig = new BackupConfiguration();
		backupConfig.setEnabled( false );
		Settings settings = new Settings();
		settings.setIpConfiguration( ipConfig );
		settings.setBackupConfiguration( backupConfig );
		instance.setSettings( settings );
		return new GcpSqlInstance( "insecure-sql", instance );
	}

	@Test
	void should_return_all_sql_instances() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpSqlInstance.class, List.of( secureInstance(), insecureInstance() ) );

			var result = session.createQuery(
					"SELECT i.resourceId, i.payload.name FROM GcpSqlInstance i",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_detect_public_ip_disabled() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpSqlInstance.class, List.of( secureInstance(), insecureInstance() ) );

			var result = session.createQuery(
					"""
					SELECT i.resourceId,
						i.payload.name,
						COALESCE(i.payload.settings.ipConfiguration.ipv4Enabled, false) = false AS passed
					FROM GcpSqlInstance i
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( true, false );
		}
	}

	@Test
	void should_detect_backup_enabled() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpSqlInstance.class, List.of( secureInstance(), insecureInstance() ) );

			var result = session.createQuery(
					"""
					SELECT i.resourceId,
						i.payload.name,
						COALESCE(i.payload.settings.backupConfiguration.enabled, false) = true AS passed
					FROM GcpSqlInstance i
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( true, false );
		}
	}
}

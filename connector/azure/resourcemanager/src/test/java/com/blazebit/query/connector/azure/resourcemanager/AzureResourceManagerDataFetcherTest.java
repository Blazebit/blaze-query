/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AzureResourceManagerDataFetcherTest {

private static final QueryContext CONTEXT;

static {
	var builder = new QueryContextBuilderImpl();
	builder.registerSchemaProvider(new AzureResourceManagerSchemaProvider());
	builder.registerSchemaObject(
		AzureResourceManagedCluster.class, ManagedClusterDataFetcher.INSTANCE);
	builder.registerSchemaObject(
		AzureResourceNetworkSecurityGroup.class, NetworkSecurityGroupDataFetcher.INSTANCE);
	builder.registerSchemaObjectAlias(AzureResourceManagedCluster.class, "AzureManagedCluster");
	builder.registerSchemaObjectAlias(
		AzureResourceNetworkSecurityGroup.class, "AzureNetworkSecurityGroup");
	builder.registerSchemaObjectAlias(
		AzureResourcePostgreSqlFlexibleServer.class, "AzurePostgreSqlFlexibleServer");
	builder.registerSchemaObjectAlias(
		AzureResourcePostgreSqlFlexibleServerBackup.class, "AzurePostgreSqlFlexibleServerBackup");
	builder.registerSchemaObjectAlias(
		AzureResourcePostgreSqlFlexibleServerWithParameters.class,
		"AzurePostgreSqlFlexibleServerParameters");
	CONTEXT = builder.build();
}

@Test
void should_return_cluster() {
	try (var session = CONTEXT.createSession()) {
	session.put(
		AzureResourceManagedCluster.class,
		Collections.singletonList(AzureTestObjects.azureKubernetesService()));

	var typedQuery =
		session.createQuery(
			"select mc.* from AzureManagedCluster mc",
			new TypeReference<Map<String, Object>>() {});

	assertThat(typedQuery.getResultList()).isNotEmpty();
	}
}

@Test
void should_return_nsg() {
	try (var session = CONTEXT.createSession()) {
	session.put(
		AzureResourceNetworkSecurityGroup.class,
		List.of(
			AzureTestObjects.azureNetworkSecurityGroupSshAllowed(),
			AzureTestObjects.azureNetworkSecurityGroupRdpAllowed()));

	var typedQuery =
		session.createQuery(
			"select nsg.payload.id from AzureNetworkSecurityGroup nsg where exists (select 1 from unnest(nsg.payload.securityRules) as r where r.direction = 'Inbound' and r.access = 'Allow' and r.destinationPortRange = 3389 )",
			new TypeReference<Map<String, Object>>() {});

	assertThat(typedQuery.getResultList())
		.extracting(result -> result.get("id"))
		.containsExactly(
			"/subscriptions/e864bc3e-3581-473d-bc31-757e489cf8fa/resourceGroups/virtualmachines/providers/Microsoft.Network/networkSecurityGroups/windows-vm-no-automatic-patching-standard-security-type-nsg");
	}
}

@Test
void should_return_postgreflexibleserver() {
	try (var session = CONTEXT.createSession()) {
	session.put(
		AzureResourcePostgreSqlFlexibleServer.class,
		List.of(AzureTestObjects.azureResourcePostgreSqlFlexibleServer()));

	var typedQuery =
		session.createQuery(
			"select server.payload.id from AzurePostgreSqlFlexibleServer server",
			new TypeReference<Map<String, Object>>() {});

	assertThat(typedQuery.getResultList())
		.extracting(result -> result.get("id"))
		.containsExactly(
			"/subscriptions/e864bc3e-3581-473d-bc31-757e489cf8fa/resourceGroups/databases/providers/Microsoft.DBforPostgreSQL/flexibleServers/flexiblepostgresql");
	}
}

@Test
void should_return_postgresql_flexible_server_backup() {
	try (var session = CONTEXT.createSession()) {
	session.put(
		AzureResourcePostgreSqlFlexibleServerBackup.class,
		List.of(AzureTestObjects.azureResourcePostgreSqlFlexibleServerBackup()));

	var typedQuery =
		session.createQuery(
			"select backup.payload.id from AzurePostgreSqlFlexibleServerBackup backup",
			new TypeReference<Map<String, Object>>() {});

	assertThat(typedQuery.getResultList())
		.extracting(result -> result.get("id"))
		.containsExactly(
			"/subscriptions/e864bc3e-3581-473d-bc31-757e489cf8fa/resourceGroups/databases/providers/Microsoft.DBforPostgreSQL/flexibleServers/flexiblepostgresql/backups/backup_638760196676440117");
	}
}

@Test
void should_return_postgresql_flexible_server_with_parameters() {
	try (var session = CONTEXT.createSession()) {
	session.put(
		AzureResourcePostgreSqlFlexibleServerWithParameters.class,
		List.of(AzureTestObjects.azureResourcePostgreSqlFlexibleServerWithParameters()));

	var typedQuery =
		session.createQuery(
			"select server.payload.id, server.parameters from AzurePostgreSqlFlexibleServerParameters server",
			new TypeReference<Map<String, Object>>() {});

	assertThat(typedQuery.getResultList())
		.extracting(
			result -> {
				Object params = result.get("parameters");
				if (params instanceof Map<?, ?> paramsMap) {
				return paramsMap.get("someParameterKey");
				}
				return params;
			})
		.containsExactly("someParameterValue");
	}
}
}

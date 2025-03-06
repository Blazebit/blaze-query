/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.resourcemanager;

import com.azure.json.JsonOptions;
import com.azure.json.JsonReader;
import com.azure.json.implementation.DefaultJsonReader;
import com.azure.resourcemanager.compute.fluent.models.VirtualMachineInner;
import com.azure.resourcemanager.containerservice.fluent.models.ManagedClusterInner;
import com.azure.resourcemanager.keyvault.fluent.models.VaultInner;
import com.azure.resourcemanager.network.fluent.models.NetworkSecurityGroupInner;
import com.azure.resourcemanager.postgresqlflexibleserver.fluent.models.ServerBackupInner;
import com.azure.resourcemanager.postgresqlflexibleserver.fluent.models.ServerInner;
import com.azure.resourcemanager.storage.fluent.models.BlobServicePropertiesInner;
import com.azure.resourcemanager.storage.fluent.models.StorageAccountInner;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class AzureTestObjects {

private AzureTestObjects() {}

private static JsonReader jsonReader(String file) throws IOException {
	return DefaultJsonReader.fromStream(new FileInputStream(file), new JsonOptions());
}

public static VirtualMachineInner linuxVMWithPasswordEnabled() {
	try {
	return VirtualMachineInner.fromJson(
		jsonReader(
			"src/test/resources/resource-definitions/azure/virtual-machine/linux-vm-with-password-enabled.json"));
	} catch (IOException e) {
	throw new RuntimeException(e);
	}
}

public static VirtualMachineInner linuxVMWithoutPasswordEnabled() {
	try {
	return VirtualMachineInner.fromJson(
		jsonReader(
			"src/test/resources/resource-definitions/azure/virtual-machine/linux-vm-without-password-enabled.json"));
	} catch (IOException e) {
	throw new RuntimeException(e);
	}
}

public static StorageAccountInner storageAccount() {
	try {
	return StorageAccountInner.fromJson(
		jsonReader(
			"src/test/resources/resource-definitions/azure/storage-account/storage-account.json"));
	} catch (IOException e) {
	throw new RuntimeException(e);
	}
}

public static BlobServicePropertiesInner blobServiceProperties() {
	try {
	return BlobServicePropertiesInner.fromJson(
		jsonReader(
			"src/test/resources/resource-definitions/azure/storage-account/blob-service-properties.json"));
	} catch (IOException e) {
	throw new RuntimeException(e);
	}
}

public static VaultInner keyVault() {
	try {
	return VaultInner.fromJson(
		jsonReader("src/test/resources/resource-definitions/azure/key-vault/key-vault.json"));
	} catch (IOException e) {
	throw new RuntimeException(e);
	}
}

public static AzureResourceManagedCluster azureKubernetesService() {
	try {
	ManagedClusterInner managedClusterInner =
		ManagedClusterInner.fromJson(
			jsonReader("src/test/resources/resource-definitions/azure/aks/aks.json"));
	return new AzureResourceManagedCluster("123", managedClusterInner.id(), managedClusterInner);
	} catch (IOException e) {
	throw new RuntimeException(e);
	}
}

public static AzureResourceNetworkSecurityGroup azureNetworkSecurityGroupRdpAllowed() {
	try {
	NetworkSecurityGroupInner networkSecurityGroupInner =
		NetworkSecurityGroupInner.fromJson(
			jsonReader(
				"src/test/resources/resource-definitions/azure/network-security-group/allow-rdp-tcp-3389-inbound.json"));
	return new AzureResourceNetworkSecurityGroup(
		"123", networkSecurityGroupInner.id(), networkSecurityGroupInner);
	} catch (IOException e) {
	throw new RuntimeException(e);
	}
}

public static AzureResourceNetworkSecurityGroup azureNetworkSecurityGroupSshAllowed() {
	try {
	NetworkSecurityGroupInner networkSecurityGroupInner =
		NetworkSecurityGroupInner.fromJson(
			jsonReader(
				"src/test/resources/resource-definitions/azure/network-security-group/allow-ssh-tcp-22-inbound.json"));
	return new AzureResourceNetworkSecurityGroup(
		"123", networkSecurityGroupInner.id(), networkSecurityGroupInner);
	} catch (IOException e) {
	throw new RuntimeException(e);
	}
}

public static AzureResourcePostgreSqlFlexibleServer azureResourcePostgreSqlFlexibleServer() {
	try {
	ServerInner postgresqlServerInner =
		ServerInner.fromJson(
			jsonReader(
				"src/test/resources/resource-definitions/azure/databases/postgresql-flexible-server/flexible-server.json"));
	return new AzureResourcePostgreSqlFlexibleServer(
		"123", postgresqlServerInner.id(), postgresqlServerInner);
	} catch (IOException e) {
	throw new RuntimeException(e);
	}
}

public static AzureResourcePostgreSqlFlexibleServerBackup
	azureResourcePostgreSqlFlexibleServerBackup() {
	try {
	ServerBackupInner postgresqlServerBackupInner =
		ServerBackupInner.fromJson(
			jsonReader(
				"src/test/resources/resource-definitions/azure/databases/postgresql-flexible-server/flexible-server-backup.json"));
	return new AzureResourcePostgreSqlFlexibleServerBackup(
		"123",
		postgresqlServerBackupInner.id(),
		postgresqlServerBackupInner,
		"flexibleServerId123");
	} catch (IOException e) {
	throw new RuntimeException(e);
	}
}

public static AzureResourcePostgreSqlFlexibleServerWithParameters
	azureResourcePostgreSqlFlexibleServerWithParameters() {
	try {
	ServerInner postgresqlServerInner =
		ServerInner.fromJson(
			jsonReader(
				"src/test/resources/resource-definitions/azure/databases/postgresql-flexible-server/flexible-server.json"));
	Map<String, String> parameters = Map.of("someParameterKey", "someParameterValue");
	return new AzureResourcePostgreSqlFlexibleServerWithParameters(
		"123", postgresqlServerInner.id(), postgresqlServerInner, parameters);
	} catch (IOException e) {
	throw new RuntimeException(e);
	}
}
}

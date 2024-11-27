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
import com.azure.resourcemanager.storage.fluent.models.BlobServicePropertiesInner;
import com.azure.resourcemanager.storage.fluent.models.StorageAccountInner;

import java.io.FileInputStream;
import java.io.IOException;

public class AzureTestObjects {

	private AzureTestObjects(){}

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

	public static ManagedClusterInner azureKubernetesService() {
		try {
			return ManagedClusterInner.fromJson(
					jsonReader(
							"src/test/resources/resource-definitions/azure/aks/aks.json"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

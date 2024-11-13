/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.azure.json.JsonOptions;
import com.azure.json.JsonReader;
import com.azure.json.implementation.DefaultJsonReader;
import com.azure.resourcemanager.compute.fluent.models.VirtualMachineInner;
import com.azure.resourcemanager.keyvault.fluent.models.VaultInner;
import com.azure.resourcemanager.storage.fluent.models.BlobServicePropertiesInner;
import com.azure.resourcemanager.storage.fluent.models.StorageAccountInner;
import com.microsoft.graph.beta.models.ConditionalAccessPolicy;
import com.microsoft.graph.beta.models.ManagedDevice;
import com.microsoft.graph.beta.models.Organization;
import com.microsoft.graph.beta.models.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class AzureTestObjects {

	private AzureTestObjects() {}

	public static VirtualMachineInner linuxVMWithPasswordEnabled() {
			return TestObject.fromJson( VirtualMachineInner.class,
					new File(
							"src/test/resources/resource-definitions/azure/virtual-machine/linux-vm-with-password-enabled.json"));

	}

	public static VirtualMachineInner linuxVMWithoutPasswordEnabled() {
			return TestObject.fromJson(VirtualMachineInner.class,
					new File(
							"src/test/resources/resource-definitions/azure/virtual-machine/linux-vm-without-password-enabled.json"));
	}

	public static ConditionalAccessPolicy globalAdministratorsMFAConditionalAccessPolicy() {
		return TestObject.fromJson(
				ConditionalAccessPolicy.class,
				new File(
						"src/test/resources/resource-definitions/azure/conditional-access-policy/global-administrators-mfa.json"));
	}

	public static StorageAccountInner storageAccount() {
			return TestObject.fromJson(StorageAccountInner.class,
					new File(
							"src/test/resources/resource-definitions/azure/storage-account/storage-account.json"));
	}

	public static BlobServicePropertiesInner blobServiceProperties() {
			return TestObject.fromJson(BlobServicePropertiesInner.class,
					new File(
							"src/test/resources/resource-definitions/azure/storage-account/blob-service-properties.json"));

	}

	public static VaultInner keyVault() {
			return TestObject.fromJson(VaultInner.class,
					new File("src/test/resources/resource-definitions/azure/key-vault/key-vault.json"));
	}

	private static JsonReader jsonReader(String file) throws IOException {
		return DefaultJsonReader.fromStream(new FileInputStream(file), new JsonOptions());
	}

	public static VaultInner keyVaultWithDefaultNetworkAcls() {
		return TestObject.fromJson(
				VaultInner.class,
				new File(
						"src/test/resources/resource-definitions/azure/key-vault/key-vault-default-network-acls.json"));
	}

	// https://graph.microsoft.com/beta/deviceManagement/managedDevices?$select=deviceName,isEncrypted,emailAddress,userPrincipalName,model,manufacturer,managementAgent
	public static ManagedDevice managedDeviceUnencrypted() {
		return TestObject.fromJson(
				ManagedDevice.class,
				new File(
						"src/test/resources/resource-definitions/azure/managed-device/device-unencrypted.json"));
	}

	public static ManagedDevice managedDeviceMac() {
		return TestObject.fromJson(
				ManagedDevice.class,
				new File(
						"src/test/resources/resource-definitions/azure/managed-device/device-mdm-mac.json"));
	}

	public static ManagedDevice managedDeviceWindows() {
		return TestObject.fromJson(
				ManagedDevice.class,
				new File(
						"src/test/resources/resource-definitions/azure/managed-device/device-mdm-windows.json"));
	}

	public static ManagedDevice managedDeviceStale() {
		return TestObject.fromJson(
				ManagedDevice.class,
				new File(
						"src/test/resources/resource-definitions/azure/managed-device/device-mdm-windows-stale.json"));
	}

	// https://graph.microsoft.com/beta/users?$select=signInActivity
	public static User staleEnabledUserWithSignInActivity() {
		return TestObject.fromJson(
				User.class,
				new File(
						"src/test/resources/resource-definitions/azure/user/user-stale-with-sign-in-activity.json"));
	}

	public static User staleDisabledUserWithSignInActivity() {
		return TestObject.fromJson(
				User.class,
				new File(
						"src/test/resources/resource-definitions/azure/user/user-stale-disabled-with-sign-in-activity.json"));
	}

	public static User serviceAccountUserNoSignInActivity() {
		return TestObject.fromJson(
				User.class, new File("src/test/resources/resource-definitions/azure/user/user-1.json"));
	}

	public static User hybridUser() {
		return TestObject.fromJson(
				User.class,
				new File("src/test/resources/resource-definitions/azure/user/user-hybrid-ad.json"));
	}

	public static Organization organization() {
		return TestObject.fromJson(
				Organization.class,
				new File("src/test/resources/resource-definitions/azure/organization/organization.json"));
	}

	public static Organization hybridOrganization() {
		return TestObject.fromJson(
				Organization.class,
				new File(
						"src/test/resources/resource-definitions/azure/organization/hybrid-organization.json"));
	}
}

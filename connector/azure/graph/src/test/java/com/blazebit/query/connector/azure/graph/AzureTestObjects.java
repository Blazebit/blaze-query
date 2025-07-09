/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import com.microsoft.graph.beta.models.ManagedDevice;
import com.microsoft.graph.beta.models.Organization;
import com.microsoft.graph.beta.models.User;
import com.microsoft.graph.beta.models.security.Alert;
import com.microsoft.graph.beta.models.security.Incident;

import java.io.File;

public final class AzureTestObjects {

	private AzureTestObjects() {}

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
	public static AzureGraphUserLastSignInActivity staleEnabledUserWithSignInActivity() {
		User user = TestObject.fromJson(
				User.class,
				new File(
						"src/test/resources/resource-definitions/azure/user/user-stale-with-sign-in-activity.json" ) );
		return new AzureGraphUserLastSignInActivity( "123", new UserLastSignInActivity( user ) );
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

	public static AzureGraphUser hybridUser() {
		User user = TestObject.fromJson(
				User.class,
				new File( "src/test/resources/resource-definitions/azure/user/user-hybrid-ad.json" ) );
		return new AzureGraphUser( "123", user );
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

	public static AzureGraphAlert alertLow() {
		var alert = TestObject.fromJson(
				Alert.class,
				new File("src/test/resources/resource-definitions/azure/security/alert1.json"));
		return new AzureGraphAlert( "123", alert );
	}

	public static AzureGraphAlert alertMedium() {
		var alert = TestObject.fromJson(
				Alert.class,
				new File("src/test/resources/resource-definitions/azure/security/alert2.json"));
		return new AzureGraphAlert( "123", alert );
	}

	public static AzureGraphIncident incidentInformational() {
		var incident = TestObject.fromJson(
				Incident.class,
				new File("src/test/resources/resource-definitions/azure/security/incident2.json"));
		return new AzureGraphIncident( "123", incident );
	}

	public static AzureGraphIncident incidentMedium() {
		var incident = TestObject.fromJson(
				Incident.class,
				new File("src/test/resources/resource-definitions/azure/security/incident1.json"));
		return new AzureGraphIncident( "123", incident );
	}
}

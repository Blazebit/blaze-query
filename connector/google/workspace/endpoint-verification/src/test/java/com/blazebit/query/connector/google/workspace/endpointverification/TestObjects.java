/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.google.workspace.endpointverification;

import com.google.api.client.util.DateTime;
import com.google.api.services.directory.model.ChromeOsDevice;
import com.google.api.services.directory.model.MobileDevice;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class TestObjects {

	private TestObjects() {
	}

	public static GoogleMobileDevice approvedEncryptedMobileDevice() {
		MobileDevice device = new MobileDevice();
		device.setDeviceId( "mobile-approved-encrypted" );
		device.setModel( "Pixel 8" );
		device.setOs( "Android" );
		device.setType( "ANDROID" );
		device.setStatus( "APPROVED" );
		device.setEncryptionStatus( "ENABLED" );
		device.setDevicePasswordStatus( "ACTIVE" );
		device.setDeviceCompromisedStatus( "No compromise detected" );
		device.setLastSync( new DateTime( Instant.now().toEpochMilli() ) );
		return new GoogleMobileDevice( device.getDeviceId(), device );
	}

	public static GoogleMobileDevice pendingMobileDevice() {
		MobileDevice device = new MobileDevice();
		device.setDeviceId( "mobile-pending" );
		device.setModel( "iPhone 15" );
		device.setOs( "iOS" );
		device.setType( "IOS" );
		device.setStatus( "PENDING" );
		device.setEncryptionStatus( "ENABLED" );
		device.setDevicePasswordStatus( "ACTIVE" );
		device.setLastSync( new DateTime( Instant.now().toEpochMilli() ) );
		return new GoogleMobileDevice( device.getDeviceId(), device );
	}

	public static GoogleMobileDevice blockedMobileDevice() {
		MobileDevice device = new MobileDevice();
		device.setDeviceId( "mobile-blocked" );
		device.setModel( "Galaxy S23" );
		device.setOs( "Android" );
		device.setType( "ANDROID" );
		device.setStatus( "BLOCKED" );
		device.setEncryptionStatus( "DISABLED" );
		device.setDevicePasswordStatus( "INACTIVE" );
		device.setLastSync( new DateTime(
				Instant.now().minus( 90, ChronoUnit.DAYS ).toEpochMilli() ) );
		return new GoogleMobileDevice( device.getDeviceId(), device );
	}

	public static GoogleMobileDevice approvedUnencryptedMobileDevice() {
		MobileDevice device = new MobileDevice();
		device.setDeviceId( "mobile-approved-unencrypted" );
		device.setModel( "Pixel 6" );
		device.setOs( "Android" );
		device.setType( "ANDROID" );
		device.setStatus( "APPROVED" );
		device.setEncryptionStatus( "DISABLED" );
		device.setDevicePasswordStatus( "ACTIVE" );
		device.setLastSync( new DateTime( Instant.now().toEpochMilli() ) );
		return new GoogleMobileDevice( device.getDeviceId(), device );
	}

	public static GoogleChromeOsDevice activeChromeOsDevice() {
		ChromeOsDevice device = new ChromeOsDevice();
		device.setDeviceId( "chromeos-active" );
		device.setSerialNumber( "SN-001" );
		device.setModel( "Chromebook Plus" );
		device.setStatus( "ACTIVE" );
		device.setOsVersion( "120.0.6099.235" );
		device.setPlatformVersion( "15359.58.0" );
		device.setFirmwareVersion( "Google_Strongbad.13672.135.0" );
		device.setOrgUnitPath( "/Engineering" );
		// AUE 2028-06-01
		device.setAutoUpdateExpiration( Instant.parse( "2028-06-01T00:00:00Z" ).toEpochMilli() );
		device.setLastSync( new DateTime( Instant.now().toEpochMilli() ) );
		device.setLastEnrollmentTime( new DateTime( "2023-01-15T08:00:00.000Z" ) );
		return new GoogleChromeOsDevice( device.getDeviceId(), device );
	}

	public static GoogleChromeOsDevice deprovisionedChromeOsDevice() {
		ChromeOsDevice device = new ChromeOsDevice();
		device.setDeviceId( "chromeos-deprovisioned" );
		device.setSerialNumber( "SN-002" );
		device.setModel( "Chromebook" );
		device.setStatus( "DEPROVISIONED" );
		device.setOsVersion( "114.0.5735.350" );
		device.setPlatformVersion( "15359.0.0" );
		device.setOrgUnitPath( "/Retired" );
		// AUE 2024-06-01
		device.setAutoUpdateExpiration( Instant.parse( "2024-06-01T00:00:00Z" ).toEpochMilli() );
		device.setLastSync( new DateTime(
				Instant.now().minus( 180, ChronoUnit.DAYS ).toEpochMilli() ) );
		return new GoogleChromeOsDevice( device.getDeviceId(), device );
	}

	public static GoogleChromeOsDevice activeChromeOsDeviceExpiredAutoUpdate() {
		ChromeOsDevice device = new ChromeOsDevice();
		device.setDeviceId( "chromeos-expired-aue" );
		device.setSerialNumber( "SN-003" );
		device.setModel( "Chromebook (Legacy)" );
		device.setStatus( "ACTIVE" );
		device.setOsVersion( "109.0.5414.125" );
		device.setPlatformVersion( "15236.80.0" );
		device.setOrgUnitPath( "/Finance" );
		// AUE 2023-06-01
		device.setAutoUpdateExpiration( Instant.parse( "2023-06-01T00:00:00Z" ).toEpochMilli() );
		device.setLastSync( new DateTime( Instant.now().toEpochMilli() ) );
		return new GoogleChromeOsDevice( device.getDeviceId(), device );
	}
}

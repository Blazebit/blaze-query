/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.connector.google.workspace.endpointverification.GoogleChromeOsDevice;
import com.blazebit.query.connector.google.workspace.endpointverification.GoogleMobileDevice;
import com.blazebit.query.connector.google.workspace.endpointverification.GoogleWorkspaceEndpointVerificationSchemaProvider;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GoogleWorkspaceEndpointVerificationTests {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new GoogleWorkspaceEndpointVerificationSchemaProvider() );
		builder.registerSchemaObjectAlias( GoogleMobileDevice.class, "GoogleMobileDevice" );
		builder.registerSchemaObjectAlias( GoogleChromeOsDevice.class, "GoogleChromeOsDevice" );
		CONTEXT = builder.build();
	}

	// -------------------------------------------------------------------------
	// Mobile device tests
	// -------------------------------------------------------------------------

	@Test
	void should_return_all_mobile_devices() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					GoogleMobileDevice.class,
					List.of(
							TestObjects.approvedEncryptedMobileDevice(),
							TestObjects.pendingMobileDevice(),
							TestObjects.blockedMobileDevice()
					)
			);

			var result = session.createQuery(
					"select d.* from GoogleMobileDevice d",
					new TypeReference<Map<String, Object>>() {}
			).getResultList();

			assertThat( result ).hasSize( 3 );
		}
	}

	@Test
	void should_return_unapproved_mobile_devices() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					GoogleMobileDevice.class,
					List.of(
							TestObjects.approvedEncryptedMobileDevice(),
							TestObjects.pendingMobileDevice(),
							TestObjects.blockedMobileDevice()
					)
			);

			var result = session.createQuery(
					"select d.* from GoogleMobileDevice d where d.mobileDevice.status <> 'APPROVED'",
					new TypeReference<Map<String, Object>>() {}
			).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result )
					.extracting( r -> (String) ( (Map<?, ?>) r.get( "mobileDevice" ) ).get( "deviceId" ) )
					.containsExactlyInAnyOrder( "mobile-pending", "mobile-blocked" );
		}
	}

	@Test
	void should_return_mobile_devices_without_encryption() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					GoogleMobileDevice.class,
					List.of(
							TestObjects.approvedEncryptedMobileDevice(),
							TestObjects.approvedUnencryptedMobileDevice(),
							TestObjects.blockedMobileDevice()
					)
			);

			var result = session.createQuery(
					"select d.* from GoogleMobileDevice d where d.mobileDevice.encryptionStatus = 'DISABLED'",
					new TypeReference<Map<String, Object>>() {}
			).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result )
					.extracting( r -> (String) ( (Map<?, ?>) r.get( "mobileDevice" ) ).get( "deviceId" ) )
					.containsExactlyInAnyOrder( "mobile-approved-unencrypted", "mobile-blocked" );
		}
	}

	@Test
	void should_return_approved_mobile_devices_without_encryption() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					GoogleMobileDevice.class,
					List.of(
							TestObjects.approvedEncryptedMobileDevice(),
							TestObjects.approvedUnencryptedMobileDevice(),
							TestObjects.blockedMobileDevice()
					)
			);

			var result = session.createQuery(
					"select d.* from GoogleMobileDevice d " +
							"where d.mobileDevice.status = 'APPROVED' " +
							"and d.mobileDevice.encryptionStatus = 'DISABLED'",
					new TypeReference<Map<String, Object>>() {}
			).getResultList();

			assertThat( result ).hasSize( 1 );
			assertThat( result )
					.extracting( r -> (String) ( (Map<?, ?>) r.get( "mobileDevice" ) ).get( "deviceId" ) )
					.containsExactly( "mobile-approved-unencrypted" );
		}
	}

	// -------------------------------------------------------------------------
	// ChromeOS device tests
	// -------------------------------------------------------------------------

	@Test
	void should_return_all_chromeos_devices() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					GoogleChromeOsDevice.class,
					List.of(
							TestObjects.activeChromeOsDevice(),
							TestObjects.deprovisionedChromeOsDevice(),
							TestObjects.activeChromeOsDeviceExpiredAutoUpdate()
					)
			);

			var result = session.createQuery(
					"select d.* from GoogleChromeOsDevice d",
					new TypeReference<Map<String, Object>>() {}
			).getResultList();

			assertThat( result ).hasSize( 3 );
		}
	}

	@Test
	void should_return_active_chromeos_devices() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					GoogleChromeOsDevice.class,
					List.of(
							TestObjects.activeChromeOsDevice(),
							TestObjects.deprovisionedChromeOsDevice(),
							TestObjects.activeChromeOsDeviceExpiredAutoUpdate()
					)
			);

			var result = session.createQuery(
					"select d.* from GoogleChromeOsDevice d where d.chromeOsDevice.status = 'ACTIVE'",
					new TypeReference<Map<String, Object>>() {}
			).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result )
					.extracting( r -> (String) ( (Map<?, ?>) r.get( "chromeOsDevice" ) ).get( "deviceId" ) )
					.containsExactlyInAnyOrder( "chromeos-active", "chromeos-expired-aue" );
		}
	}

	@Test
	void should_return_chromeos_devices_with_expired_auto_update() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					GoogleChromeOsDevice.class,
					List.of(
							TestObjects.activeChromeOsDevice(),
							TestObjects.deprovisionedChromeOsDevice(),
							TestObjects.activeChromeOsDeviceExpiredAutoUpdate()
					)
			);

			long threshold = Instant.parse( "2025-01-01T00:00:00Z" ).toEpochMilli();
			var query = session.createQuery(
					"select d.* from GoogleChromeOsDevice d " +
							"where d.chromeOsDevice.autoUpdateExpiration < ?",
					new TypeReference<Map<String, Object>>() {}
			);
			query.setParameter( 1, threshold );

			var result = query.getResultList();
			assertThat( result ).hasSize( 2 );
			assertThat( result )
					.extracting( r -> (String) ( (Map<?, ?>) r.get( "chromeOsDevice" ) ).get( "deviceId" ) )
					.containsExactlyInAnyOrder( "chromeos-deprovisioned", "chromeos-expired-aue" );
		}
	}

	@Test
	void should_return_active_chromeos_devices_with_expired_auto_update() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					GoogleChromeOsDevice.class,
					List.of(
							TestObjects.activeChromeOsDevice(),
							TestObjects.deprovisionedChromeOsDevice(),
							TestObjects.activeChromeOsDeviceExpiredAutoUpdate()
					)
			);

			long threshold = Instant.parse( "2025-01-01T00:00:00Z" ).toEpochMilli();
			var query = session.createQuery(
					"select d.* from GoogleChromeOsDevice d " +
							"where d.chromeOsDevice.status = 'ACTIVE' " +
							"and d.chromeOsDevice.autoUpdateExpiration < ?",
					new TypeReference<Map<String, Object>>() {}
			);
			query.setParameter( 1, threshold );

			var result = query.getResultList();
			assertThat( result ).hasSize( 1 );
			assertThat( result )
					.extracting( r -> (String) ( (Map<?, ?>) r.get( "chromeOsDevice" ) ).get( "deviceId" ) )
					.containsExactly( "chromeos-expired-aue" );
		}
	}
}

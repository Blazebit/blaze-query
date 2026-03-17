/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.gcp.kms;

import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import com.google.cloud.kms.v1.CryptoKey;
import com.google.cloud.kms.v1.CryptoKey.CryptoKeyPurpose;
import com.google.cloud.kms.v1.CryptoKeyVersion;
import com.google.cloud.kms.v1.CryptoKeyVersion.CryptoKeyVersionState;
import com.google.protobuf.Duration;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GcpKmsCryptoKeyTests {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new GcpKmsSchemaProvider() );
		builder.registerSchemaObjectAlias( GcpKmsCryptoKey.class, "GcpKmsCryptoKey" );
		CONTEXT = builder.build();
	}

	private static GcpKmsCryptoKey keyWithRotation() {
		// Rotation period of 30 days (2592000 seconds)
		CryptoKey key = CryptoKey.newBuilder()
				.setName( "projects/my-project/locations/global/keyRings/my-ring/cryptoKeys/key-with-rotation" )
				.setPurpose( CryptoKeyPurpose.ENCRYPT_DECRYPT )
				.setRotationPeriod( Duration.newBuilder().setSeconds( 2592000L ).build() )
				.setPrimary( CryptoKeyVersion.newBuilder()
						.setState( CryptoKeyVersionState.ENABLED )
						.build() )
				.build();
		return new GcpKmsCryptoKey( "key-with-rotation", key );
	}

	private static GcpKmsCryptoKey keyWithoutRotation() {
		CryptoKey key = CryptoKey.newBuilder()
				.setName( "projects/my-project/locations/global/keyRings/my-ring/cryptoKeys/key-no-rotation" )
				.setPurpose( CryptoKeyPurpose.ENCRYPT_DECRYPT )
				.setPrimary( CryptoKeyVersion.newBuilder()
						.setState( CryptoKeyVersionState.ENABLED )
						.build() )
				.build();
		return new GcpKmsCryptoKey( "key-no-rotation", key );
	}

	private static GcpKmsCryptoKey disabledKey() {
		CryptoKey key = CryptoKey.newBuilder()
				.setName( "projects/my-project/locations/global/keyRings/my-ring/cryptoKeys/disabled-key" )
				.setPurpose( CryptoKeyPurpose.ENCRYPT_DECRYPT )
				.setPrimary( CryptoKeyVersion.newBuilder()
						.setState( CryptoKeyVersionState.DISABLED )
						.build() )
				.build();
		return new GcpKmsCryptoKey( "disabled-key", key );
	}

	@Test
	void should_return_all_crypto_keys() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpKmsCryptoKey.class, List.of( keyWithRotation(), keyWithoutRotation(), disabledKey() ) );

			var result = session.createQuery(
					"SELECT k.resourceId, k.payload.name FROM GcpKmsCryptoKey k",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 3 );
		}
	}

	@Test
	void should_detect_key_rotation_configured() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpKmsCryptoKey.class, List.of( keyWithRotation(), keyWithoutRotation() ) );

			var result = session.createQuery(
					"""
					SELECT k.resourceId,
						k.payload.name,
						k.payload.rotationPeriod IS NOT NULL AS passed
					FROM GcpKmsCryptoKey k
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( true, false );
		}
	}

	@Test
	void should_detect_key_purpose() {
		try (var session = CONTEXT.createSession()) {
			CryptoKey encryptDecryptKey = CryptoKey.newBuilder()
					.setName( "projects/p/locations/global/keyRings/r/cryptoKeys/enc-dec" )
					.setPurpose( CryptoKeyPurpose.ENCRYPT_DECRYPT )
					.build();
			CryptoKey signVerifyKey = CryptoKey.newBuilder()
					.setName( "projects/p/locations/global/keyRings/r/cryptoKeys/sign-verify" )
					.setPurpose( CryptoKeyPurpose.ASYMMETRIC_SIGN )
					.build();
			GcpKmsCryptoKey encDecKey = new GcpKmsCryptoKey( "enc-dec", encryptDecryptKey );
			GcpKmsCryptoKey sigKey = new GcpKmsCryptoKey( "sign-verify", signVerifyKey );

			session.put( GcpKmsCryptoKey.class, List.of( encDecKey, sigKey ) );

			var result = session.createQuery(
					"""
					SELECT k.resourceId,
						k.payload.purpose = 'ENCRYPT_DECRYPT' AS isEncryptDecrypt
					FROM GcpKmsCryptoKey k
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "isEncryptDecrypt" ) )
					.containsExactlyInAnyOrder( true, false );
		}
	}
}

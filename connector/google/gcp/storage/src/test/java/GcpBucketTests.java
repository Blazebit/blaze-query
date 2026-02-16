/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
import com.blazebit.query.QueryContext;
import com.blazebit.query.TypeReference;
import com.blazebit.query.connector.gcp.storage.GcpBucket;
import com.blazebit.query.connector.gcp.storage.GcpStorageSchemaProvider;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import com.google.protobuf.Duration;
import com.google.protobuf.Timestamp;
import com.google.storage.v2.Bucket;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GcpBucketTests {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new GcpStorageSchemaProvider() );
		builder.registerSchemaObjectAlias( GcpBucket.class, "GcpBucket" );
		CONTEXT = builder.build();
	}

	private static GcpBucket bucketWithoutPolicies() {
		Bucket bucket = Bucket.newBuilder()
				.setName( "projects/_/buckets/tidal-test-bucket" )
				.setBucketId( "tidal-test-bucket" )
				.setLocation( "EUROPE-WEST4" )
				.setLocationType( "region" )
				.setStorageClass( "STANDARD" )
				.setEtag( "CAE=" )
				.setProject( "projects/203985346109" )
				.setMetageneration( 1 )
				.setCreateTime( Timestamp.newBuilder().setSeconds( 1739465737 ).setNanos( 770000000 ).build() )
				.setUpdateTime( Timestamp.newBuilder().setSeconds( 1739465737 ).setNanos( 770000000 ).build() )
				.setBilling( Bucket.Billing.newBuilder().setRequesterPays( true ).build() )
				.setVersioning( Bucket.Versioning.newBuilder().setEnabled( false ).build() )
				.setIamConfig( Bucket.IamConfig.newBuilder()
						.setUniformBucketLevelAccess( Bucket.IamConfig.UniformBucketLevelAccess.newBuilder()
								.setEnabled( true )
								.setLockTime( Timestamp.newBuilder().setSeconds( 1747241737 ).setNanos( 770000000 ).build() )
								.build() )
						.setPublicAccessPrevention( "inherited" )
						.build() )
				// retentionPolicy left unset (default instance)
				// softDeletePolicy left unset (default instance)
				.setEncryption( Bucket.Encryption.getDefaultInstance() )
				.setLogging( Bucket.Logging.getDefaultInstance() )
				.build();
		return new GcpBucket( "projects/_/buckets/tidal-test-bucket", bucket );
	}

	private static GcpBucket bucketWithPolicies() {
		Bucket bucket = Bucket.newBuilder()
				.setName( "projects/_/buckets/tidal-test-bucket-with-policies" )
				.setBucketId( "tidal-test-bucket-with-policies" )
				.setLocation( "EUROPE-WEST4" )
				.setLocationType( "region" )
				.setStorageClass( "STANDARD" )
				.setEtag( "CAE=" )
				.setProject( "projects/203985346109" )
				.setMetageneration( 1 )
				.setCreateTime( Timestamp.newBuilder().setSeconds( 1739638960 ).setNanos( 180000000 ).build() )
				.setUpdateTime( Timestamp.newBuilder().setSeconds( 1739638960 ).setNanos( 180000000 ).build() )
				.setBilling( Bucket.Billing.newBuilder().setRequesterPays( true ).build() )
				.setVersioning( Bucket.Versioning.newBuilder().setEnabled( true ).build() )
				.setIamConfig( Bucket.IamConfig.newBuilder()
						.setUniformBucketLevelAccess( Bucket.IamConfig.UniformBucketLevelAccess.newBuilder()
								.setEnabled( true )
								.setLockTime( Timestamp.newBuilder().setSeconds( 1747415560 ).setNanos( 180000000 ).build() )
								.build() )
						.setPublicAccessPrevention( "enforced" )
						.build() )
				.setRetentionPolicy( Bucket.RetentionPolicy.newBuilder()
						.setEffectiveTime( Timestamp.newBuilder().setSeconds( 1739638960 ).setNanos( 180000000 ).build() )
						.setRetentionDuration( Duration.newBuilder().setSeconds( 604800 ).build() )
						.build() )
				.setSoftDeletePolicy( Bucket.SoftDeletePolicy.newBuilder()
						.setEffectiveTime( Timestamp.newBuilder().setSeconds( 1739638960 ).setNanos( 180000000 ).build() )
						.setRetentionDuration( Duration.newBuilder().setSeconds( 604800 ).build() )
						.build() )
				.setEncryption( Bucket.Encryption.newBuilder()
						.setDefaultKmsKey( "projects/my-project/locations/global/keyRings/my-ring/cryptoKeys/my-key" )
						.build() )
				.build();
		return new GcpBucket( "projects/_/buckets/tidal-test-bucket-with-policies", bucket );
	}

	@Test
	void should_return_all_buckets() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpBucket.class, List.of( bucketWithoutPolicies(), bucketWithPolicies() ) );

			var result = session.createQuery(
					"SELECT b.resourceId, b.payload.name FROM GcpBucket b",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
		}
	}

	@Test
	void should_detect_uniform_bucket_level_access() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpBucket.class, List.of( bucketWithoutPolicies(), bucketWithPolicies() ) );

			var result = session.createQuery(
					"""
					SELECT b.resourceId,
						b.payload.name,
						COALESCE(b.payload.iamConfig.uniformBucketLevelAccess.enabled, false) = true AS passed
					FROM GcpBucket b
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).allSatisfy( row ->
					assertThat( row.get( "passed" ) ).isEqualTo( true ) );
		}
	}

	@Test
	void should_detect_public_access_prevention() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpBucket.class, List.of( bucketWithoutPolicies(), bucketWithPolicies() ) );

			var result = session.createQuery(
					"""
					SELECT b.resourceId,
						b.payload.name,
						COALESCE(b.payload.iamConfig.publicAccessPrevention, '') = 'enforced' AS passed
					FROM GcpBucket b
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( false, true );
		}
	}

	@Test
	void should_detect_versioning_enabled() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpBucket.class, List.of( bucketWithoutPolicies(), bucketWithPolicies() ) );

			var result = session.createQuery(
					"""
					SELECT b.resourceId,
						b.payload.name,
						COALESCE(b.payload.`versioning`.enabled, false) = true AS passed
					FROM GcpBucket b
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( false, true );
		}
	}

	@Test
	void should_detect_cmek_encryption() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpBucket.class, List.of( bucketWithoutPolicies(), bucketWithPolicies() ) );

			var result = session.createQuery(
					"""
					SELECT b.resourceId,
						b.payload.name,
						COALESCE(b.payload.encryption.defaultKmsKey, '') <> '' AS passed
					FROM GcpBucket b
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( false, true );
		}
	}

	@Test
	void should_detect_retention_policy_enabled() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpBucket.class, List.of( bucketWithoutPolicies(), bucketWithPolicies() ) );

			var result = session.createQuery(
					"""
					SELECT b.resourceId,
						b.payload.name,
						b.payload.retentionPolicy.retentionDuration IS NOT NULL AS passed
					FROM GcpBucket b
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( false, true );
		}
	}

	@Test
	void should_detect_soft_delete_policy_enabled() {
		try (var session = CONTEXT.createSession()) {
			session.put( GcpBucket.class, List.of( bucketWithoutPolicies(), bucketWithPolicies() ) );

			var result = session.createQuery(
					"""
					SELECT b.resourceId,
						b.payload.name,
						b.payload.softDeletePolicy.retentionDuration IS NOT NULL AS passed
					FROM GcpBucket b
					""",
					new TypeReference<Map<String, Object>>() {} ).getResultList();

			assertThat( result ).hasSize( 2 );
			assertThat( result ).extracting( r -> r.get( "passed" ) )
					.containsExactlyInAnyOrder( false, true );
		}
	}
}

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.iam;

import com.blazebit.query.QueryContext;
import com.blazebit.query.impl.QueryContextBuilderImpl;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.iam.model.MFADevice;
import software.amazon.awssdk.services.iam.model.PasswordPolicy;
import software.amazon.awssdk.services.iam.model.User;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AwsIAMSchemaProviderTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AwsIAMSchemaProvider() );
		builder.registerSchemaObjectAlias( User.class, "AwsIAMUser" );
		builder.registerSchemaObjectAlias( PasswordPolicy.class, "AwsIAMPasswordPolicy" );
		builder.registerSchemaObjectAlias( MFADevice.class, "AwsIAMMFADevice" );
		builder.registerSchemaObjectAlias( AccountSummary.class, "AwsIAMAccountSummary" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_users() {
		try (var session = CONTEXT.createSession()) {
			session.put(
					User.class, Collections.singletonList( TestObjects.userWithMfa() ) );

			var typedQuery =
					session.createQuery( "select u.* from AwsIAMUser u", Map.class );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_password_policy() {
		try (var session = CONTEXT.createSession()) {
			session.put( PasswordPolicy.class, TestObjects.defaultAccountPasswordPolicy() );

			var typedQuery =
					session.createQuery( "select p.* from AwsIAMPasswordPolicy p", Map.class );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_mfa_device() {
		try (var session = CONTEXT.createSession()) {
			session.put( MFADevice.class, Collections.singletonList( TestObjects.mfaDevice() ) );

			var typedQuery =
					session.createQuery(
							"select m.* from AwsIAMMFADevice m", Map.class );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_account_summary() {
		try (var session = CONTEXT.createSession()) {
			session.put( AccountSummary.class, Collections.singletonList( TestObjects.accountSummary() ) );

			var typedQuery =
					session.createQuery(
							"select a.* from AwsIAMAccountSummary a", Map.class );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}
}

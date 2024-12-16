/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.aws.ec2;

import com.blazebit.query.QueryContext;
import com.blazebit.query.impl.QueryContextBuilderImpl;

import org.junit.jupiter.api.Test;


import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AwsEc2SchemaProviderTest {

	private static final QueryContext CONTEXT;

	static {
		var builder = new QueryContextBuilderImpl();
		builder.registerSchemaProvider( new AwsEc2SchemaProvider() );
		builder.registerSchemaObjectAlias( AwsInstance.class, "AwsEc2Instance" );
		builder.registerSchemaObjectAlias( AwsVpc.class, "AwsEc2Vpc" );
		builder.registerSchemaObjectAlias( AwsSecurityGroup.class, "AwsEc2SecurityGroup" );
		builder.registerSchemaObjectAlias( AwsVolume.class, "AwsEc2Volume" );
		builder.registerSchemaObjectAlias( AwsNetworkAcl.class, "AwsEc2NetworkAcl" );
		CONTEXT = builder.build();
	}

	@Test
	void should_return_ec2_instances() {
		try (var session = CONTEXT.createSession()) {
			session.put( AwsInstance.class, Collections.singletonList( TestObjects.instance() ) );

			var typedQuery =
					session.createQuery( "select i.* from AwsEc2Instance i", Map.class );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}

	@Test
	void should_return_ec2_volumes() {
		try (var session = CONTEXT.createSession()) {
			session.put( AwsVolume.class, Collections.singletonList( TestObjects.volume() ) );

			var typedQuery =
					session.createQuery( "select v.* from AwsEc2Volume v", Map.class );

			assertThat( typedQuery.getResultList() ).isNotEmpty();
		}
	}
}

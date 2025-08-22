/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.impl;

import com.blazebit.query.QueryContext;
import com.blazebit.query.QuerySession;
import com.blazebit.query.TypedQuery;
import com.blazebit.query.connector.base.ConventionContext;
import com.blazebit.query.connector.base.DataFormats;
import com.blazebit.query.spi.DataFetchContext;
import com.blazebit.query.spi.DataFetcher;
import com.blazebit.query.spi.DataFormat;
import com.blazebit.query.spi.Queries;
import com.blazebit.query.spi.QueryContextBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Christian Beikov
 * @since 1.0.16
 */
public class EnumArrayTest {

	// Test for #68
	@Test
	public void test() {
		JiraCloudAdminUser model = new JiraCloudAdminUser(
				"u1",
				List.of( PlatformRole.ADMIN )
		);
		QueryContextBuilder queryContextBuilder = Queries.createQueryContextBuilder();
		queryContextBuilder.registerSchemaObject( JiraCloudAdminUser.class, new DataFetcher<>() {
			@Override
			public DataFormat getDataFormat() {
				return DataFormats.componentMethodConvention( JiraCloudAdminUser.class, ConventionContext.NO_FILTER );
			}

			@Override
			public List<JiraCloudAdminUser> fetch(DataFetchContext context) {
				return List.of( model );
			}
		} );
		queryContextBuilder.registerSchemaObjectAlias( JiraCloudAdminUser.class, "JiraCloudAdminUser" );
		try (QueryContext queryContext = queryContextBuilder.build()) {
			try (QuerySession session = queryContext.createSession()) {
				TypedQuery<Object[]> query = session.createQuery(
						"select r = 'ADMIN' " +
								"from JiraCloudAdminUser u " +
								"cross join unnest(u.platformRoles) r"
				);
				List<Object[]> result = query.getResultList();
				assertEquals( 1, result.size() );
			}
		}
	}

	public record JiraCloudAdminUser(
			String id,
			List<PlatformRole> platformRoles) {
	}

	enum PlatformRole {
		ADMIN,
		USER
	}
}

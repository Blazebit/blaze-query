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
public class ArrayTest {

	// Test for #63
	@Test
	public void test() {
		GitHubRepository model = new GitHubRepository(
				"repo",
				List.of( new GitHubBranchProtectionRule( List.of( "main" ) ) )
		);
		QueryContextBuilder queryContextBuilder = Queries.createQueryContextBuilder();
		queryContextBuilder.registerSchemaObject( GitHubRepository.class, new DataFetcher<>() {
			@Override
			public DataFormat getDataFormat() {
				return DataFormats.componentMethodConvention( GitHubRepository.class, ConventionContext.NO_FILTER );
			}

			@Override
			public List<GitHubRepository> fetch(DataFetchContext context) {
				return List.of( model );
			}
		} );
		queryContextBuilder.registerSchemaObjectAlias( GitHubRepository.class, "GitHubRepository" );
		try (QueryContext queryContext = queryContextBuilder.build()) {
			try (QuerySession session = queryContext.createSession()) {
				TypedQuery<Object[]> query = session.createQuery(
						"select r.id " +
								"from GitHubRepository as r " +
								"where exists (" +
								"select 1 " +
								"from unnest(r.branchProtectionRules) as br " +
								"where br.field1 is null " +
								"and exists(select 1 from GitHubRepository x)" +
								")"
				);
				List<Object[]> result = query.getResultList();
				assertEquals( 1, result.size() );
			}
		}
	}

	public record GitHubRepository(
			String id,
			List<GitHubBranchProtectionRule> branchProtectionRules) {
	}

	// Need 7 fields so that FlatLists.ComparableListImpl is chosen, which provokes this bug
	public record GitHubBranchProtectionRule(
			String field1,
			String field2,
			String field3,
			String field4,
			String field5,
			String field6,
			List<String> matchingRefs) {
		public GitHubBranchProtectionRule(List<String> matchingRefs) {
			this(
					null,
					null,
					null,
					null,
					null,
					null,
					matchingRefs
			);
		}
	}
}

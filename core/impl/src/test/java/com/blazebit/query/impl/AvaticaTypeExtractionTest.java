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
import org.apache.calcite.avatica.ColumnMetaData.AvaticaType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AvaticaTypeExtractionTest {

	@Test
	public void testAvaticaTypeExtraction() throws Exception {
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
				// We use a query that will produce a complex result (Map) to ensure normalizeValue is called with types
				TypedQuery<Map> query = session.createQuery(
						"select r.id as id, r.branchProtectionRules as rules from GitHubRepository r",
						Map.class
				);
				List<Map> result = query.getResultList();
				assertEquals( 1, result.size() );
				Map row = result.get( 0 );
				assertEquals( "repo", row.get( "id" ) );

				// The rules column is a list of structs.
				// Without correct AvaticaType extraction, normalizeValue might not be able to fully convert it
				// if it relies on StructType information for recursive normalization.
				Object rules = row.get( "rules" );
				assertNotNull( rules );
				assertTrue( rules instanceof List );
				List rulesList = (List) rules;
				assertEquals( 1, rulesList.size() );
				Object rule = rulesList.get( 0 );
				// If it's correctly normalized via StructType, it should be a Map (see normalizeValue logic for Struct)
				assertTrue( rule instanceof Map, "Rule should be normalized to a Map but was: " + rule.getClass() );
				Map ruleMap = (Map) rule;
				assertEquals( List.of( "main" ), ruleMap.get( "matchingRefs" ) );
			}
		}
	}

	@Test
	public void testFallbackWhenNotAvaticaResultSet() throws Exception {
		Method extractColumnTypes = QueryContextImpl.class.getDeclaredMethod( "extractColumnTypes", ResultSet.class );
		extractColumnTypes.setAccessible( true );

		// We can't easily mock ResultSet without Mockito, so we'll just use null or a simple proxy if needed.
		// Since the code uses isWrapperFor, null will probably throw NPE if not careful,
		// but isWrapperFor(AvaticaResultSet.class) on a non-wrapper should return false or throw.

		// Actually, QueryContextImpl.extractColumnTypes has a try-catch (Exception e) that returns new AvaticaType[0].
		// So passing null should just return empty array.
		AvaticaType[] types = (AvaticaType[]) extractColumnTypes.invoke( null, (Object) null );
		assertNotNull( types );
		assertEquals( 0, types.length );
	}

	@Test
	public void testNestedArrayExtraction() throws Exception {
		GitHubRepository model = new GitHubRepository(
				"repo",
				List.of( new GitHubBranchProtectionRule( List.of( "main", "develop" ) ) )
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
				// Select just the rules (List of Structs)
				TypedQuery<List> query = session.createQuery(
						"select r.branchProtectionRules from GitHubRepository r",
						List.class
				);
				List<List> result = query.getResultList();
				assertEquals( 1, result.size() );
				List rules = result.get( 0 );
				assertNotNull( rules );
				assertEquals( 1, rules.size() );
				Object rule = rules.get( 0 );
				assertTrue( rule instanceof Map, "Rule should be normalized to a Map but was: " + rule.getClass() );
				Map ruleMap = (Map) rule;
				assertEquals( List.of( "main", "develop" ), ruleMap.get( "matchingRefs" ) );
			}
		}
	}


	@Test
	public void testPrimitiveTypes() throws Exception {
		PrimitiveModel model = new PrimitiveModel( 1L, "test", true, 3.14 );
		QueryContextBuilder queryContextBuilder = Queries.createQueryContextBuilder();
		queryContextBuilder.registerSchemaObject( PrimitiveModel.class, new DataFetcher<>() {
			@Override
			public DataFormat getDataFormat() {
				return DataFormats.componentMethodConvention( PrimitiveModel.class, ConventionContext.NO_FILTER );
			}

			@Override
			public List<PrimitiveModel> fetch(DataFetchContext context) {
				return List.of( model );
			}
		} );
		queryContextBuilder.registerSchemaObjectAlias( PrimitiveModel.class, "PrimitiveModel" );
		try (QueryContext queryContext = queryContextBuilder.build()) {
			try (QuerySession session = queryContext.createSession()) {
				TypedQuery<Object[]> query = session.createQuery(
						"select p.id, p.name, p.active, p.val from PrimitiveModel p",
						Object[].class
				);
				List<Object[]> result = query.getResultList();
				assertEquals( 1, result.size() );
				Object[] row = result.get( 0 );
				assertEquals( 1L, row[0] );
				assertEquals( "test", row[1] );
				assertEquals( true, row[2] );
				assertEquals( 3.14, row[3] );
			}
		}
	}

	public record PrimitiveModel(Long id, String name, boolean active, double val) {
	}

	public record GitHubRepository(
			String id,
			List<GitHubBranchProtectionRule> branchProtectionRules) {
	}

	public record GitHubBranchProtectionRule(
			List<String> matchingRefs) {
	}
}
